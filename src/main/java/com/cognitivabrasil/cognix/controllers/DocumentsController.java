/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.cognix.controllers;

import cognitivabrasil.obaa.Relation.Kind;
import cognitivabrasil.obaa.Relation.Relation;
import com.cognitivabrasil.cognix.entities.Document;
import com.cognitivabrasil.cognix.entities.dto.DocumentDto;
import com.cognitivabrasil.cognix.entities.dto.MessageDto;
import com.cognitivabrasil.cognix.services.DocumentService;
import java.io.IOException;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Document's controller to get, put and update Cognix documents.
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RestController
@RequestMapping("/documents")
public class DocumentsController {

    @Autowired
    private DocumentService docService;
    private final Logger log = LoggerFactory.getLogger(DocumentsController.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<MessageDto> handleException() {
        MessageDto error = new MessageDto(MessageDto.ERROR, "O documento solicitado não foi encontrado.");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public HttpEntity<DocumentDto> get(@PathVariable Integer id) {

        Document d = docService.get(id);
        if (d.isDeleted()) {
            return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
        }
        d.getMetadata().setLocale("pt-BR");
        DocumentDto dto = new DocumentDto(d);
        //mostrar a relação para o usuário
        if (d.getMetadata() != null && !d.getMetadata().getRelations().isEmpty()) {
            for (Relation rel : d.getMetadata().getRelations()) {
                switch (rel.getKind().getText()) {
                    case Kind.IS_VERSION_OF:
                        if (!rel.getResource().getIdentifier().isEmpty()) {
                            dto.setIsversion(rel.getResource().getIdentifier().get(0).getEntry());
                        }
                        break;
                    case Kind.HAS_VERSION:
                        if (!rel.getResource().getIdentifier().isEmpty()) {
                            dto.setHasVersion(rel.getResource().getIdentifier().get(0).getEntry());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @GetMapping("/{id}/json")
    public String getJson(@PathVariable Integer id) {
        Document d = docService.get(id);
        d.getMetadata().setLocale("pt-BR");
        return d.getMetadata().getJson();
    }

    @DeleteMapping("/{id}/delete")
    public HttpEntity<MessageDto> delete(@PathVariable("id") int id) {
        MessageDto msg;

        log.info("Deletando o objeto: " + id);
        try {
            Document d = docService.get(id);

            if (d.isDeleted()) {
                msg = new MessageDto(MessageDto.ERROR, "O documento solicitado já foi deletado.");
                return new ResponseEntity(msg, HttpStatus.MOVED_PERMANENTLY);
            }

            //TODO: Security. Isso aqui tem que implementar depois de ter o security.
//            if (!isManagerForThisDocument(d, request)) {
//                msg = new MessageDto(MessageDto.ERROR, "Acesso negado! Você não ter permissão para deletar este documento.");
//                return new ResponseEntity(msg, HttpStatus.FORBIDDEN);
//            }
            docService.delete(d);
            msg = new MessageDto(MessageDto.SUCCESS, "Documento excluido com sucesso");
        } catch (IOException io) {
            msg = new MessageDto(MessageDto.SUCCESS, "Documento excluido com sucesso, mas os seus arquivos não foram encontrados");
            log.warn("Documento excluido com sucesso, mas os seus arquivos não foram encontrados", io);
        } catch (DataAccessException e) {
            log.error("Não foi possivel excluir o documento.", e);
            msg = new MessageDto(MessageDto.ERROR, "Erro ao excluir o documento.", "");
        }
        return new ResponseEntity(msg, HttpStatus.OK);
    }

    @GetMapping("/{id}/edit")
    public HttpEntity<DocumentDto> edit(@PathVariable("id") Integer id) throws IOException {
        Document d = docService.get(id);

        //TODO: Security. Isso aqui tem que implementar depois de ter o security.
//            if (!isManagerForThisDocument(d, request)) {
//                msg = new MessageDto(MessageDto.ERROR, "Acesso negado! Você não ter permissão para deletar este documento.");
//                return new ResponseEntity(msg, HttpStatus.FORBIDDEN);
//            }
        return new ResponseEntity<>(new DocumentDto(d), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public HttpEntity<MessageDto> editDo(@PathVariable("id") Integer id, @RequestBody DocumentDto dto) throws IOException {
        Document d = docService.get(id);
        //TODO: Security. Isso aqui tem que implementar depois de ter o security.
//            if (!isManagerForThisDocument(d, request)) {
//                msg = new MessageDto(MessageDto.ERROR, "Acesso negado! Você não ter permissão para deletar este documento.");
//                return new ResponseEntity(msg, HttpStatus.FORBIDDEN);
//            }
        setOBAAFiles(d, dto);
        MessageDto msg = new MessageDto(MessageDto.SUCCESS, "Documento editado com sucesso");
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    private void setOBAAFiles(Document d, DocumentDto dto) {
        log.debug("Trying to save");

        Subject s;

        if (d != null && d.getMetadata() != null && d.getMetadata().getGeneral() != null) {
            List<String> keysObaa = d.getMetadata().getGeneral().getKeywords();
            List<Subject> allSubjects = subService.getAll();
            String nameSubject = "";
            for (String key : keysObaa) {
                for(Subject subject : allSubjects){
                    if(subject.getName().equals(retiraAcentos(key).toLowerCase())){
                         nameSubject = retiraAcentos(key).toLowerCase();
                    }
                }
            }
            log.trace("Assunto do OA: " + nameSubject);
            if (!nameSubject.equals("")) {
                s = subService.getSubjectByName(nameSubject);
                d.setSubject(s);
            }
        }


        OBAA obaa = dto.getMetadata();

        // split the keywords
        List<Keyword> splittedKeywords = new ArrayList<>();
        if (!obaa.getGeneral().getKeywords().isEmpty()) {

            for (String k : obaa.getGeneral().getKeywords()) {
                for (String nk : k.split("\\s*[,;]\\s*")) {
                    splittedKeywords.add(new Keyword(nk));
                }
            }
            obaa.getGeneral().setKeywords(splittedKeywords);
        }

        log.debug("Title: " + obaa.getGeneral().getTitles());

        Technical t = obaa.getTechnical();

        Long size;

        size = 0L;
        for (Files f : d.getFiles()) {
            size += f.getSizeInBytes();
        }

        // somatorio to tamanho de todos os arquivos
        t.setSize(size);
        obaa.setTechnical(t);
        d.setMetadata(obaa);

        if (obaa.getTechnical() == null) {
            log.warn("Technical was null");
            obaa.setTechnical(new Technical());
        }
        List<Location> l = obaa.getTechnical().getLocation();

        //if doesn't have location, an entry based is created
        if (l == null || l.isEmpty()) {
            obaa.getTechnical().addLocation(obaa.getGeneral().getIdentifiers().get(0).getEntry());
        }

        // Preenchimento dos metametadados
        Metametadata meta = new Metametadata();

        meta.setLanguage("pt-BR");

        // logged user data
        User currentUser = UsersController.getCurrentUser();
        String userName = currentUser.getUsername();
        Contribute c = new Contribute();

        // Quando fizer o cadastro dos usuários do sistema cuidar para que possa por os dados do vcard
        Entity e = new Entity();
        e.setName(userName, "");

        c.addEntity(e);
        c.setRole(Role.AUTHOR);

        // today date
        Date date = new Date();
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        c.setDate(dateFormat.format(date));
        meta.addContribute(c);

        Identifier i = new Identifier("URI", "http://www.w3.org/2001/XMLSchema-instance");

        meta.addIdentifier(i);

        //default metadataSchema
        MetadataSchema metaSchema = new MetadataSchema();
        meta.addSchema(metaSchema);

        d.getMetadata().setMetametadata(meta);

        //Parsing do duration
        d.setObaaEntry(obaa.getGeneral().getIdentifiers().get(0).getEntry());

        //Se o documento tem uma relação is_version_of, é testado se o outro
        //documento tem o Has_version, se não tiver a relação é criada.
        for (Identifier id : obaa.getRelationsWithKind(Kind.IS_VERSION_OF)) {
            if (id.getCatalog().equalsIgnoreCase("URI")) {
                Document docVersion = docService.get(id.getEntry());
                if (docVersion.getMetadata() != null) {
                    //testa se o documento ja tem tem a versao cadastrada
                    if (!docVersion.getMetadata().hasRelationWith(Kind.HAS_VERSION, d.getObaaEntry())) {
                        //Cria relação de versão no orginial
                        Relation originalRelation = new Relation();
                        originalRelation.setKind(Kind.HAS_VERSION);
                        originalRelation.setResource(new Resource());
                        originalRelation.getResource().addIdentifier(new Identifier("URI", d.getObaaEntry()));
                        List<Relation> relationsList = new ArrayList<>();
                        relationsList.add(originalRelation);
                        docVersion.getMetadata().setRelations(relationsList);
                        //salva o documento com a nova relaçao
                        docService.save(docVersion);
                    }
                }
            }
        }

        d.setMetadata(obaa);
        d.setActive(true);
        docService.save(d);
    }

    private String createUri(Document d) {
        return Config.getUrl(config)+d.getId();
    }

}

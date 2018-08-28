/*
 * Copyright (c) 2018 Cognitiva Brasil Tecnologias Educacionais
 * http://www.cognitivabrasil.com.br
 *
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 * contato@cognitivabrasil.com.br for information.
 */
package com.cognitivabrasil.cognix.controllers;

import cognitivabrasil.obaa.General.General;
import cognitivabrasil.obaa.General.Identifier;
import cognitivabrasil.obaa.OBAA;
import cognitivabrasil.obaa.Relation.Kind;
import cognitivabrasil.obaa.Relation.Relation;
import cognitivabrasil.obaa.Relation.Resource;
import com.cognitivabrasil.cognix.entities.Document;
import com.cognitivabrasil.cognix.entities.Files;
import com.cognitivabrasil.cognix.entities.User;
import com.cognitivabrasil.cognix.services.DocumentService;
import com.cognitivabrasil.cognix.util.Config;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentControllerTest {

    private MockMvc mvc;

    @Mock
    private DocumentService docService;

    @Mock
    private Config config;

    @InjectMocks
    private DocumentsController controller;

    private Document d;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        d = new Document();
        d.setId(10);
        d.setObaaEntry("entry1");
        d.setActive(true);
        d.setCreated(DateTime.parse("2018-08-21T10:05"));
        OBAA obaa = new OBAA();
        General general = new General();
        general.addIdentifier(new Identifier("URI", "www.cognitivabrasil.com.br/12"));
        general.addTitle("Título aleatório");
        obaa.setGeneral(general);
        d.setMetadata(obaa);
        Files file = new Files();
        file.setId(1);
        file.setName("thumbnail.png");
        file.setContentType("image/png");
        file.setDocument(d);
        file.setLocation("http://addrs/thumbnail.png");
        file.setPartialSize(1024);
        d.setFiles(Arrays.asList(file));
        d.setOwner(new User("mfnunes", "Marcos Nunes"));

        given(docService.get(10)).willReturn(d);
    }

    @Test
    public void testGetAll() throws Exception {

        Document d2 = new Document();
        d2.setId(11);
        d2.setObaaEntry("entry2");
        Document d3 = new Document();
        d3.setId(12);
        d3.setObaaEntry("entry3");

        List<Document> docs = new ArrayList<>(Arrays.asList(d, d2, d3));
        Page<Document> result = new PageImpl<>(docs);

        given(docService.getPage(PageRequest.of(0, 10))).willReturn(result);

        mvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", equalTo(10)))
                .andExpect(jsonPath("$[0].title", equalTo("Título aleatório")))
                .andExpect(jsonPath("$[1].id", equalTo(11)))
                .andExpect(jsonPath("$[1].title", nullValue()))
                .andExpect(jsonPath("$[2].id", equalTo(12)))
                .andExpect(jsonPath("$[2].title", nullValue()));
    }

    @Test
    public void testGetAllPageable() throws Exception {
        given(docService.getPage(any())).willReturn(new PageImpl<>(new ArrayList<>()));
        mvc.perform(get("/documents").param("page", "2").param("size", "5"))
                .andExpect(status().isOk());
        verify(docService).getPage(PageRequest.of(2, 5));
    }

    @Test
    public void testGet() throws Exception {
        Relation r = new Relation();
        r.setKind(Kind.IS_VERSION_OF);
        Resource resource = new Resource();
        resource.addIdentifier(new Identifier("cat", "entryOriginal"));
        r.setResource(resource);

        Relation r2 = new Relation();
        r2.setKind(Kind.HAS_VERSION);
        Resource resource2 = new Resource();
        resource2.addIdentifier(new Identifier("cat", "entryChild"));
        r2.setResource(resource2);
        d.getMetadata().addRelation(r);
        d.getMetadata().addRelation(r2);

        Files file = d.getFiles().get(0);

        mvc.perform(get("/documents/10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(d.getId())))
                .andExpect(jsonPath("$.obaaEntry", equalTo(d.getObaaEntry())))
                .andExpect(jsonPath("$.isVersion", equalTo("entryOriginal")))
                .andExpect(jsonPath("$.hasVersion", equalTo("entryChild")))
                .andExpect(jsonPath("$.files", hasSize(1)))
                .andExpect(jsonPath("$.files[0].document").doesNotExist())
                .andExpect(jsonPath("$.files[0].id", equalTo(file.getId())))
                .andExpect(jsonPath("$.files[0].name", equalTo(file.getName())))
                .andExpect(jsonPath("$.files[0].location", equalTo(file.getLocation())))
                .andExpect(jsonPath("$.files[0].contentType", equalTo(file.getContentType())))
                .andExpect(jsonPath("$.files[0].sizeInBytes", equalTo(file.getSizeInBytes().intValue())))
                .andExpect(jsonPath("$.files[0].randomName", nullValue()))
                .andExpect(jsonPath("$.metadata.general.titles[0]", equalTo("Título aleatório")))
                .andExpect(jsonPath("$.metadata.general.identifiers[0].catalog", equalTo("URI")))
                .andExpect(jsonPath("$.metadata.general.identifiers[0].entry", equalTo("www.cognitivabrasil.com.br/12")))
                .andExpect(jsonPath("$.metadata.relations[0].kind", equalTo("É Versão de")))
                .andExpect(jsonPath("$.metadata.relations[0].resource.identifier[0].catalog", equalTo("cat")))
                .andExpect(jsonPath("$.metadata.relations[0].resource.identifier[0].entry", equalTo("entryOriginal")))
                .andExpect(jsonPath("$.metadata.relations[1].kind", equalTo("Tem Versões")))
                .andExpect(jsonPath("$.metadata.relations[1].resource.identifier[0].catalog", equalTo("cat")))
                .andExpect(jsonPath("$.metadata.relations[1].resource.identifier[0].entry", equalTo("entryChild")));
    }

    @Test
    public void testDelete() throws Exception {
        mvc.perform(delete("/documents/10"))
                .andExpect(status().isOk());
        verify(docService).delete(d);
    }

    @Test
    public void testDeleteDocumentDeleted() throws Exception {
        d.setDeleted(true);
        mvc.perform(delete("/documents/10"))
                .andExpect(status().isMovedPermanently());
        verify(docService, times(0)).delete(d);
    }

    @Test
    public void testDeleteFileError() throws Exception {
        doThrow(new IOException("Fake error")).when(docService).delete(d);
        mvc.perform(delete("/documents/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",
                        equalTo("Documento excluido com sucesso, mas os seus arquivos não foram encontrados")));
        verify(docService).delete(d);
    }
}

/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/
package com.cognitivabrasil.cognix.controllers;

import com.cognitivabrasil.cognix.entities.dto.InformationsDto;
import com.cognitivabrasil.cognix.services.DocumentService;
import com.cognitivabrasil.cognix.util.Config;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RequestMapping("/panel")
@RestController
public class InformationPanelController {

    @Autowired
    private DocumentService docService;

    private Environment repInfo;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Config configService;

    private final Logger log = LoggerFactory.getLogger(InformationPanelController.class);

    @GetMapping
    public HttpEntity<InformationsDto> showInformations(Model model) {
        List<InformationsDto> info = new ArrayList<>();
        info.add(new InformationsDto("Versão", repInfo.getProperty("repository.version")));
        info.add(new InformationsDto("Número de documentos", Long.toString(docService.count())));

        info.add(new InformationsDto("Domínio", repInfo.getProperty("repository.hostname"),
                "Este dominio será utilizado para criar a localização dos documentos. Editar no arquivo: 'config.properties'."));
        info.add(new InformationsDto("Raiz do projeto", repInfo.getProperty("repository.rootPath", "/repositorio")));
        info.add(new InformationsDto("Porta", repInfo.getProperty("repository.port", "8080"),
                "Porta informada no arquivo '/WEB-INF/classes/config.properties'"));
        info.add(new InformationsDto("URL dos objetos", configService.getUrl() + "{id}",
                "URL que será utilizada para criar o location dos objetos. Pode ser editada em: '/WEB-INF/classes/config.properties'"));
        try {
            DatabaseMetaData databaseInfo = dataSource.getConnection().getMetaData();
            info.add(new InformationsDto("Base de dados utilizada", databaseInfo.getDatabaseProductName()));
            info.add(new InformationsDto("Versão da base de dados", databaseInfo.getDatabaseProductVersion()));
            info.add(new InformationsDto("JDBC driver", databaseInfo.getDriverName()));
            info.add(new InformationsDto("Versão do JDBC driver", databaseInfo.getDriverVersion()));
            info.add(new InformationsDto("URL da base de dados", databaseInfo.getURL()));
            info.add(new InformationsDto("Usuário da base de dados", databaseInfo.getUserName()));

        } catch (SQLException s) {
            log.error("Error getting information about database.", s);
            info.add(new InformationsDto("Erro", "Não foi possível carregar os dados da base de dados"));
        }

        return new ResponseEntity(info, HttpStatus.OK);
    }
}

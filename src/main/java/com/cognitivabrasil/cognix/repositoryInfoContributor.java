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
package com.cognitivabrasil.cognix;

import com.cognitivabrasil.cognix.services.DocumentService;
import com.cognitivabrasil.cognix.util.Config;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@Component
public class repositoryInfoContributor implements InfoContributor {

    @Autowired
    private DocumentService docService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Config config;

    private final Logger log = LoggerFactory.getLogger(repositoryInfoContributor.class);

    @Override
    public void contribute(Info.Builder builder) {
//        TODO: IMPORTANTE! Configurar o spring security para não expor para qualquer um as informações do actuator. url: /actuator/*
        builder.withDetail("documents", docService.count());
        builder.withDetail("locationUrl", config.getUrl());

        try {
            DatabaseMetaData databaseInfo = dataSource.getConnection().getMetaData();

            Map<String, String> dataBaseDetail = new HashMap<>();
            dataBaseDetail.put("productName", databaseInfo.getDatabaseProductName());
            dataBaseDetail.put("productVersion", databaseInfo.getDatabaseProductVersion());
            dataBaseDetail.put("driverName", databaseInfo.getDriverName());
            dataBaseDetail.put("driverVersion", databaseInfo.getDriverName());
            dataBaseDetail.put("url", databaseInfo.getURL());
            dataBaseDetail.put("username", databaseInfo.getUserName());

            builder.withDetail("database", dataBaseDetail);
        } catch (SQLException s) {
            log.error("Error getting information about database.", s);
        }

    }
}

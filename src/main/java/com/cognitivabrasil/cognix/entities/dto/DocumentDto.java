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
package com.cognitivabrasil.cognix.entities.dto;

import cognitivabrasil.obaa.OBAA;
import com.cognitivabrasil.cognix.entities.Document;
import com.cognitivabrasil.cognix.entities.Files;
import java.util.List;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class DocumentDto {

    private final Integer id;
    private final String obaaEntry;
    private String isVersion;
    private String hasVersion;
    private final List<Files> files;
    private final OBAA metadata;

    public DocumentDto(Document d) {
        this.id = d.getId();
        this.obaaEntry = d.getObaaEntry();
        this.files = d.getFiles();
        this.metadata = d.getMetadata();
    }

    public Integer getId() {
        return id;
    }

    public String getObaaEntry() {
        return obaaEntry;
    }

    public List<Files> getFiles() {
        return files;
    }

    public String getIsVersion() {
        return isVersion;
    }

    public void setIsVersion(String isversion) {
        this.isVersion = isversion;
    }

    public String getHasVersion() {
        return hasVersion;
    }

    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }

    public OBAA getMetadata() {
        return metadata;
    }

}

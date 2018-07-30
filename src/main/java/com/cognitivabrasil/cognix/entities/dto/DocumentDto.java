/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.cognix.entities.dto;

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
    private String isversion;
    private String hasVersion;
    private final List<Files> files;
    private final String metadata;

    public DocumentDto(Document d) {
        this.id = d.getId();
        this.obaaEntry = d.getObaaEntry();
        this.files = d.getFiles();
        this.metadata = d.getMetadata().getJson();
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

    public String getIsversion() {
        return isversion;
    }

    public void setIsversion(String isversion) {
        this.isversion = isversion;
    }

    public String getHasVersion() {
        return hasVersion;
    }

    public void setHasVersion(String hasVersion) {
        this.hasVersion = hasVersion;
    }

    public String getMetadata() {
        return metadata;
    }

}

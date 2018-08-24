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

import com.cognitivabrasil.cognix.entities.Document;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class DocumentTinyDto {

    private final int id;
    private final String title;

    public DocumentTinyDto(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public DocumentTinyDto(Document d){
        this.id = d.getId();
        this.title = d.getTitle();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

}

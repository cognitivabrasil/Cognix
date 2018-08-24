/*
 * *******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************
 *
 */
package com.cognitivabrasil.cognix.entities.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import org.junit.Test;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class MessageDtoTest {

    private final String MSG = "Mensagem de erro!";
    private final String HREF = "www.marcos.nunes";

    @Test
    public void testContrutor() {
        MessageDto msg = new MessageDto(MessageDto.ERROR, MSG, HREF);
        assertThat(msg.getType(), equalTo(MessageDto.ERROR));
        assertThat(msg.getMessage(), equalTo(MSG));
        assertThat(msg.getHref(), equalTo(HREF));
    }

    @Test
    public void testContrutor2() {
        MessageDto msg = new MessageDto(MessageDto.ERROR, MSG);
        assertThat(msg.getType(), equalTo(MessageDto.ERROR));
        assertThat(msg.getMessage(), equalTo(MSG));
        assertThat(msg.getHref(), nullValue());
    }

    @Test
    public void testSetter() {
        MessageDto msg = new MessageDto();
        msg.setType(MessageDto.ERROR);
        msg.setMessage(MSG);
        msg.setHref(HREF);

        assertThat(msg.getType(), equalTo(MessageDto.ERROR));
        assertThat(msg.getMessage(), equalTo(MSG));
        assertThat(msg.getHref(), equalTo(HREF));
    }

}

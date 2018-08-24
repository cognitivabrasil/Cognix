/*
 * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 * contato@cognitivabrasil.com.br for information.
 *
 */
package com.cognitivabrasil.cognix.repositories;

import com.cognitivabrasil.cognix.entities.Document;
import com.cognitivabrasil.cognix.entities.User;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class DocumentRepositoryITest {

    @Autowired
    private DocumentRepository docRep;

    @Test
    public void testGetAll() {
        assertThat(docRep.findAll(), hasSize(7));
    }

    @Test
    public void testGetAllToBeDelete() {
        DateTime d = DateTime.now();

        List<Document> docs = docRep.findByCreatedLessThanAndActiveIsFalse(d.minusHours(3));

        assertThat(docs.size(), equalTo(1));
    }

    @Test
    public void testGetAllNotDeleted() {
        List<Document> docs = docRep.findByDeletedIsFalseAndObaaXmlNotNullOrderByCreatedDesc();
        assertThat(docs, notNullValue());
        assertThat(docs.size(), equalTo(4));

        assertThat(docs.get(0).getId(), equalTo(7));
        assertThat(docs.get(1).getId(), equalTo(6));
        assertThat(docs.get(2).getId(), equalTo(5));
        assertThat(docs.get(3).getId(), equalTo(1));
    }

    @Test
    public void testGetByObaaEntry() {
        Document d = docRep.findByObaaEntry("entry2");
        assertThat(d.getId(), equalTo(2));
    }

    @Test
    public void testGetByOwner() {
        User u = new User();
        u.setId(2);
        long docs = docRep.countByOwnerAndDeletedIsFalseAndActiveIsTrue(u);

        assertThat(docs, equalTo(3l));
    }
}

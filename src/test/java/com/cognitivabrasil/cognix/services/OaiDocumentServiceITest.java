/*
 * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 * contato@cognitivabrasil.com.br for information.
 *
 */
package com.cognitivabrasil.cognix.services;

import ORG.oclc.oai.server.catalog.OaiDocumentService;
import com.cognitivabrasil.cognix.entities.Document;
import java.util.Iterator;
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
public class OaiDocumentServiceITest {

    @Autowired
    private OaiDocumentService oaiService;

    @Test
    public void testCount() {
        int docs = oaiService.count(null, null);
        assertThat(docs, equalTo(5));
    }

    @Test
    public void testCountBetween() {
        DateTime from = DateTime.parse("2013-05-08T03:00:01.000Z");
        DateTime until = DateTime.parse("2013-08-21T08:10:00.000Z");
        int docs = oaiService.count(from.toLocalDateTime().toDate(), until.toLocalDateTime().toDate());
        assertThat(docs, equalTo(2));

    }

    @Test
    public void testCountUntil() {

        DateTime until = DateTime.parse("2013-05-08T03:10:01.000Z");

        int docs = oaiService.count(null, until.toLocalDateTime().toDate());
        assertThat(docs, equalTo(3));
    }

    @Test
    public void testCountFrom() {

        DateTime from = DateTime.parse("2013-05-08T03:10:01.000Z");
        int docs = oaiService.count(from.toLocalDateTime().toDate(), null);
        assertThat(docs, equalTo(3));
    }

    @Test
    public void testFindAllPageable() {
        Iterator i = oaiService.find(null, null, 0, 2);
        Document d = (Document) i.next();
        assertThat(d.getId(), equalTo(2));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(1));
        assertThat(i.hasNext(), equalTo(false));

        i = oaiService.find(null, null, 2, 2);
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(5));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(6));
        assertThat(i.hasNext(), equalTo(false));

        i = oaiService.find(null, null, 4, 2);
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(7));
        assertThat(i.hasNext(), equalTo(false));

        i = oaiService.find(null, null, 6, 2);
        assertThat(i.hasNext(), equalTo(false));
    }

    @Test
    public void testFindFrom() {
        DateTime from = DateTime.parse("2013-05-08T03:10:01.000Z");
        Iterator i = oaiService.find(from.toLocalDateTime().toDate(), null, 0, 2);
        Document d = (Document) i.next();
        assertThat(d.getId(), equalTo(5));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(6));
        assertThat(i.hasNext(), equalTo(false));

        i = oaiService.find(from.toLocalDateTime().toDate(), null, 2, 2);
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(7));
        assertThat(i.hasNext(), equalTo(false));

        i = oaiService.find(from.toLocalDateTime().toDate(), null, 4, 2);
        assertThat(i.hasNext(), equalTo(false));
    }

    @Test
    public void testFindUntil() {
        DateTime until = DateTime.parse("2013-05-08T03:10:01.000Z");

        Iterator i = oaiService.find(null, until.toLocalDateTime().toDate(), 0, 2);
        Document d = (Document) i.next();
        assertThat(d.getId(), equalTo(2));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(1));
        assertThat(i.hasNext(), equalTo(false));

        i = oaiService.find(null, until.toLocalDateTime().toDate(), 2, 2);
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(5));
        assertThat(i.hasNext(), equalTo(false));
    }

    @Test
    public void testFindBetween() {
        DateTime from = DateTime.parse("2013-05-08T03:10:01.000Z");
        DateTime until = DateTime.parse("2013-08-21T08:10:00.000Z");

        Iterator i = oaiService.find(from.toLocalDateTime().toDate(), until.toLocalDateTime().toDate(), 0, 2);
        Document d = (Document) i.next();
        assertThat(d.getId(), equalTo(5));
        d = (Document) i.next();
        assertThat(d.getId(), equalTo(6));
        assertThat(i.hasNext(), equalTo(false));

        i = oaiService.find(from.toLocalDateTime().toDate(), until.toLocalDateTime().toDate(), 2, 2);
        assertThat(i.hasNext(), equalTo(false));
    }

}

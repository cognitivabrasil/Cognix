/*
 *
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************
 *
 */
package com.cognitivabrasil.cognix.entities;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import org.junit.Test;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class UserTest {

    @Test
    public void testEqual(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");

        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setPassword("nunes");
        b.setRole("admin");
        b.setUsername("marcos");

        assertThat(a.equals(b), equalTo(true));
    }

    @Test
    public void testEqualError(){
        User a = new User();
        a.setName("Marcos");

        String nome = "Marcos";
        assertThat(a.equals(nome), equalTo(false));

    }

    @Test
    public void testEqualError2(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");

        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setPassword("nunes");
        b.setRole("admin");

        assertThat(a.equals(b), equalTo(false));
    }

    @Test
    public void testEqualError3(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");

        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setPassword("nunes");
        b.setUsername("marcos");

        assertThat(a.equals(b), equalTo(false));
    }

    @Test
    public void testEqualError4(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");

        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setRole("admin");
        b.setUsername("marcos");

        assertThat(a.equals(b), equalTo(false));
    }

    @Test
    public void testEqualError5(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");

        User b = new User();
        b.setId(1);
        b.setPassword("nunes");
        b.setRole("admin");
        b.setUsername("marcos");

        assertThat(a.equals(b), equalTo(false));
    }

    @Test
    public void testEqualError6(){
        User a = new User();
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");

        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        b.setPassword("nunes");
        b.setRole("admin");
        b.setUsername("marcos");

        assertThat(a.equals(b), equalTo(false));
    }

    @Test
    public void testEqualError7(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");

        User b = new User();
        b.setId(1);
        b.setName("Marcos");
        a.setUsername("marcos");
        b.setPassword("nunes");

        assertThat(a.equals(b), equalTo(false));
    }

    @Test
    public void testEqualError8(){
        User a = new User();
        a.setId(1);
        a.setName("Marcos");
        a.setPassword("nunes");
        a.setRole("admin");
        a.setUsername("marcos");

        User b = new User();
        b.setId(1);
        assertThat(a.equals(b), equalTo(false));
        b = new User();
        b.setName("Marcos");
        assertThat(a.equals(b), equalTo(false));
        b = new User();
        b.setPassword("nunes");
        assertThat(a.equals(b), equalTo(false));
        b = new User();
        b.setRole("admin");
        assertThat(a.equals(b), equalTo(false));
        b = new User();
        b.setUsername("marcos");
        assertThat(a.equals(b), equalTo(false));

    }

    @Test
    public void testGetRoleName(){
        User a = new User();
        String result = a.getRoleNameText(); //teste de para ver se da nullPointer
        assertThat(result, nullValue());

        a.setRole(User.ROLE_ROOT);
        assertThat(a.getRoleNameText(), equalTo("Superusu\u00e1rio"));
        a.setRole(User.ROLE_DOC_ADMIN);
        assertThat(a.getRoleNameText(), equalTo("Administrador de documentos"));
        a.setRole(User.ROLE_AUTHOR);
        assertThat(a.getRoleNameText(), equalTo("Criador de documentos"));
        a.setRole(User.ROLE_VIEW);
        assertThat(a.getRoleNameText(), equalTo("Somente visualizar"));
    }

    @Test
    public void testHasPermission(){
        User u = new User();
        u.setRole(User.ROLE_DOC_ADMIN);

        assertThat(u.hasPermission(User.CREATE_DOC), equalTo(true));
    }

    @Test
    public void testHasPermissionFalse(){
        User u = new User();
        u.setRole(User.ROLE_VIEW);

        assertThat(u.hasPermission(User.CREATE_DOC), equalTo(false));
    }

}

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
package com.cognitivabrasil.cognix.services;

import com.cognitivabrasil.cognix.entities.Document;
import com.cognitivabrasil.cognix.entities.User;
import com.cognitivabrasil.cognix.repositories.UserRepository;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author marcos
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserServiceImplITest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRep;

    @Autowired
    private DocumentService docService;
    @PersistenceContext
    private EntityManager em;

//    @Autowired UserDetailsService userDetailService;
    @Test
    public void testGetUser() {
        User u = userService.get(2);

        assertThat(u.getName(), equalTo("Marcos Nunes"));
        assertThat(u.getUsername(), equalTo("marcos"));
        assertThat(u.getPassword(), equalTo("698dc19d489c4e4db73e28a713eab07b"));
        assertThat(u.getRole(), equalTo(User.ROLE_ROOT));
        assertThat(u.getRoleNameText(), equalTo("Superusuário"));
        assertThat(u.isDeleted(), equalTo(false));
    }

//    @Test (expected=UsernameNotFoundException.class)
//    public void testGetUserByUsernameError(){
//        userDetailService.loadUserByUsername("marcola");
//    }
    @Test
    public void testSaveUser() {
        String nome = "Marcos Nunes";
        String login = "nunes";

        User u = new User();
        u.setUsername(login);
        u.setName(nome);
        u.setPassword("698dc19d489c4e4db73e28a713eab07b");

        userService.save(u);

        User u2 = userService.get(login);
        assertThat(u2.getName(), equalTo(nome));
        assertThat(u2.getUsername(), equalTo(login));
        assertThat(u2.getPassword(), equalTo("698dc19d489c4e4db73e28a713eab07b"));
    }

    @Test
    public void testDeleteUser() {
        int sizeAllBefore = userRep.findAll().size();
        int sizeBefore = userService.getAll().size();

        //Definir o user 1 como root para que seja possivel deletar o 2.
        User u = userService.get(1);
        u.setRole(User.ROLE_ROOT);
        userService.save(u);

        userService.delete(userService.get(2));

        assertThat(userService.getAll().size(), equalTo(sizeBefore - 1));
        assertThat(userRep.findAll().size(), equalTo(sizeAllBefore));
    }

    /**
     * Se o usuario nao for owner de nenhum documento, entao tem que deletar da base.
     */
    @Test
    public void testDeleteUserWithoutDoc() {
        int sizeAllBefore = userRep.findAll().size();
        int sizeBefore = userService.getAll().size();
        User u = userService.get(5);
        userService.delete(u);

        assertThat(userService.getAll().size(), equalTo(sizeBefore - 1));
        assertThat(userRep.findAll().size(), equalTo(sizeAllBefore - 1));
    }

    /**
     * Se o usuario só for owner de documento deletado, entao tem que ser deletado da base.
     */
    @Test
    public void testDeleteUserDocDeleted() {
        int idUser = 1;

        Document d = docService.get(2);
        assertThat(d.isDeleted(), equalTo(true));

        int sizeAllBefore = userRep.findAll().size();
        int sizeBefore = userService.getAll().size();
        User u = userService.get(idUser);
        userService.delete(u);

        assertThat(userService.getAll().size(), equalTo(sizeBefore - 1));
        assertThat(userRep.findAll().size(), equalTo(sizeAllBefore - 1));

        em.clear();
        em.flush();

        d = docService.get(2);
        assertThat(d, notNullValue());
        assertThat(d.getOwner(), nullValue());
    }

    @Test
    public void testEditUser() {
        User u = userService.get("marcos");
        int id = u.getId();
        String username = u.getUsername();
        u.setPassword("c5e3539121c4944f2bbe097b425ee774");
        userService.save(u);

        User u2 = userService.get("marcos");
        assertThat(u2.getId(), equalTo(id));
        assertThat(u2.getUsername(), equalTo(username));
        assertThat(u2.getPassword(), equalTo("c5e3539121c4944f2bbe097b425ee774"));
    }



    @Test
    public void testDeletedUser() {
        User u = userService.get("user4");
        assertThat(u, notNullValue());
        assertThat(u.isDeleted(), equalTo(true));
    }

    @Test
    public void testHasDocument() {
        User u = new User();
        u.setId(2);
        boolean hasDocument = userService.hasDocument(userService.get(2));
        assertThat(hasDocument, equalTo(true));

        u = new User();
        u.setId(1);
        hasDocument = userService.hasDocument(u);
        assertThat(hasDocument, equalTo(false));
    }

    @Test
    public void testGetDeleted() {
        List<User> users = userService.getDeleted();

        assertThat(users, hasSize(1));

        assertThat(users.get(0).isDeleted(), equalTo(true));

    }

    @Test
    public void testActivateUser() {
        int sizeBefore = userService.getAll().size();
        int numDel = userService.getDeleted().size();
        User u = userService.get(4);
        userService.activate(u);
        assertThat(userService.getAll().size(), equalTo(sizeBefore + 1));
        assertThat(userService.getDeleted().size(), equalTo(numDel - 1));
    }

    @Test
    public void testIsLastAdmin() {
        userService.delete(userService.get(1));
        User u = userService.get(2);
        assertThat(userService.isLastAdmin(u), equalTo(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteLastAdmin() {
        User u = userService.get("marcos");
        userService.delete(u);
    }
}

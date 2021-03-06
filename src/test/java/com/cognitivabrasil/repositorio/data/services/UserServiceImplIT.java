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
package com.cognitivabrasil.repositorio.data.services;

import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.data.repositories.UserRepository;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.UserService;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 *
 * @author marcos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UserServiceImplIT extends AbstractTransactionalJUnit4SpringContextTests {
    
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRep;
    
    @Autowired
    DocumentService docService;
    @PersistenceContext
    private EntityManager em;
    
    @Autowired UserDetailsService userDetailService;
    
    @Test
    public void testGetUser(){
        User u = userService.get(2);
        
        assertThat(u.getName(), equalTo("Marcos Nunes"));
        assertThat(u.getUsername(), equalTo("marcos"));
        assertThat(u.getPassword(), equalTo("698dc19d489c4e4db73e28a713eab07b"));
        assertThat(u.getRole(), equalTo(User.ROLE_ROOT));
        assertThat(u.getRoleNameText(), equalTo("Superusuário"));
        assertThat(u.getPermissions(), hasSize(4));
        assertThat(u.isDeleted(), equalTo(false));
    }
    
    @Test
    public void testGetUserByUsername(){
        UserDetails u = userDetailService.loadUserByUsername("marcos");
        
        assertThat(u.getUsername(), equalTo("marcos"));
        assertThat(u.getPassword(), equalTo("698dc19d489c4e4db73e28a713eab07b"));
    }
    
    @Test (expected=UsernameNotFoundException.class)
    public void testGetUserByUsernameError(){
        userDetailService.loadUserByUsername("marcola");
    }
    
    @Test
    public void testSaveUser(){
        String nome = "Marcos Nunes";
        String login = "nunes";
        
        User u = new User();
        u.setUsername(login);
        u.setName(nome);
        u.setPassword("teste");
        
        userService.save(u);
        
        User u2 = userService.get(login);
        assertThat(u2.getName(), equalTo(nome));
        assertThat(u2.getUsername(), equalTo(login));
        assertThat(u2.getPassword(), equalTo("698dc19d489c4e4db73e28a713eab07b"));
    }
    
    @Test
    public void testDeleteUser(){
        int sizeAllBefore = userRep.findAll().size();
        int sizeBefore = userService.getAll().size();
        
        //Definir o user 1 como root para que seja possivel deletar o 2.
        User u = userService.get(1);
        u.setRole(User.ROLE_ROOT);
        userService.save(u);
        
        userService.delete(userService.get(2));
        
        assertThat(userService.getAll().size(), equalTo(sizeBefore-1));
        assertThat(userRep.findAll().size(), equalTo(sizeAllBefore));
    }
    
    /**
     * Se o usuario nao for owner de nenhum documento, entao tem que deletar da base.
     */
    @Test
    public void testDeleteUserWithoutDoc(){
        int sizeAllBefore = userRep.findAll().size();
        int sizeBefore = userService.getAll().size();
        User u = userService.get(5);
        userService.delete(u);
        
        assertThat(userService.getAll().size(), equalTo(sizeBefore-1));
        assertThat(userRep.findAll().size(), equalTo(sizeAllBefore-1));
    }
    
    /**
     * Se o usuario só for owner de documento deletado, entao tem que ser deletado da base.
     */
    @Test
    public void testDeleteUserDocDeleted(){
        int idUser = 1;
        
        Document d = docService.get(2);
        assertThat(d.isDeleted(), equalTo(true));
        
        int sizeAllBefore = userRep.findAll().size();
        int sizeBefore = userService.getAll().size();
        User u = userService.get(idUser);
        userService.delete(u);
        
        assertThat(userService.getAll().size(), equalTo(sizeBefore-1));
        assertThat(userRep.findAll().size(), equalTo(sizeAllBefore-1));
        
        em.clear();
        em.flush();
        
        d = docService.get(2);
        assertThat(d, notNullValue());
        assertThat(d.getOwner(), nullValue());
    }
    
    @Test
    public void testEditUser(){
        User u = userService.get("marcos");
        int id = u.getId();
        String username = u.getUsername();
        u.setPassword("marcos");
        userService.save(u);
        
        User u2 = userService.get("marcos");
        assertThat(u2.getId(), equalTo(id));
        assertThat(u2.getUsername(), equalTo(username));
        assertThat(u2.getPassword(), equalTo("c5e3539121c4944f2bbe097b425ee774"));
    }

    
    @Test
    public void testAuthenticate(){
        User u = userService.authenticate("marcos", "teste");
        assertNotNull(u);
    }
    
    @Test
    public void testAuthenticateError(){
        
        assertNull(userService.authenticate("marcos", "senhaErrada"));
        
        assertNull(userService.authenticate(null, "teste"));
        
        assertNull(userService.authenticate("usuarioErrado", "teste"));
    }
    
    @Test
    public void testDeletedUser(){
        User u = userService.get("user4");
        assertThat(u, notNullValue());
        assertThat(u.isDeleted(), equalTo(true));
        assertThat(u.isEnabled(), equalTo(false));
        assertThat(u.isCredentialsNonExpired(), equalTo(false));
    }
    
    @Test
    public void testHasDocument(){
        User u = new User();
        u.setId(2);
        boolean hasDocument = userService.hasDocument(u);
        assertThat(hasDocument, equalTo(true));
        
         u = new User();
        u.setId(1);
        hasDocument = userService.hasDocument(u);
        assertThat(hasDocument, equalTo(false));
    }
    
    @Test
    public void testGetDeleted(){
        List<User> users = userService.getDeleted();
        
        assertThat(users, hasSize(1));
        
        assertThat(users.get(0).isDeleted(), equalTo(true));
        
    }
    
    @Test
    public void testActivateUser(){
        int sizeBefore = userService.getAll().size();
        int numDel = userService.getDeleted().size();
        User u = userService.get(4);
        userService.activate(u);
        assertThat(userService.getAll().size(), equalTo(sizeBefore+1));
        assertThat(userService.getDeleted().size(), equalTo(numDel-1));
    }
    
    @Test
    public void testIsLastAdmin(){
        userService.delete(userService.get(1));
        User u = userService.get(2);
        assertThat(userService.isLastAdmin(u), equalTo(true));        
    }
    
    @Test(expected = IllegalStateException.class)
    public void testDeleteLastAdmin(){
        User u = userService.get("marcos");
        userService.delete(u);
    }
}


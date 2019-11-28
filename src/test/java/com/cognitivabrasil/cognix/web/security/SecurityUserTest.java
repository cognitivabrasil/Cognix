/*
 * Copyright (c) 2019 Cognitiva Brasil Tecnologias Educacionais
 * http://www.cognitivabrasil.com.br
 *
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 * contato@cognitivabrasil.com.br for information.
 */
package com.cognitivabrasil.cognix.web.security;

import com.cognitivabrasil.cognix.entities.User;
import com.cognitivabrasil.cognix.repositories.UserRepository;
import com.cognitivabrasil.cognix.services.UserService;
import java.security.Principal;
import javax.servlet.Filter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author igor
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class SecurityUserTest {
    
    private final String TOKEN_NAME = TokenAuthenticationService.AUTH_HEADER_NAME;

    @MockBean
    private AuthenticationManager authenticationManager;
    
     @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private TokenHandler tokenHandler;

    private MockMvc mvc;

    private User u;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    @InjectMocks
    private CustomUserDetailsService customUserService;
    
    

    private SecurityUser user;
    
     @Mock
    private UserRepository repository;
    
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(springSecurityFilterChain)
                .build();
        
        
        when(repository.findByUsername("no_such_user")).thenReturn(null);
        
 
        u = new User();
        u.setId(1);

        u.setPassword("magma");

        u.setUsername("profzin");
        u.setName("profzin");
        
        u.setRole("ROLE_USER");
        
        when(userService.get(1)).thenReturn(u);
        when(userService.get("profzin")).thenReturn(u);

        
    }
    
    //Testa as possibilidades de Get /user/* que um admin pode ter
    @Test
    public void testAdminCanGetUser() throws Exception {
        String token = tokenHandler.createTokenForUser(new SecurityUser(u));
        

        System.out.println(token);
        Principal logged = () -> u.getUsername();
         this.mvc.perform(get("/users").header(TOKEN_NAME, token))
                .andExpect(status().is2xxSuccessful());
         
    }
    
}

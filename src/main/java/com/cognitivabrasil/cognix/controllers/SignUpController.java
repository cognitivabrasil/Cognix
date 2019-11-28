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
package com.cognitivabrasil.cognix.controllers;

import com.cognitivabrasil.cognix.entities.User;
import com.cognitivabrasil.cognix.services.UserService;
import com.cognitivabrasil.cognix.web.security.SecurityUser;
import com.cognitivabrasil.cognix.web.security.TokenHandler;
import com.cognitivabrasil.cognix.web.security.UserForm;
import java.util.Arrays;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author igor
 */
@RestController
@RequestMapping("/signup")
public class SignUpController {
    
    @Autowired
    UserService userService;
    
    @Autowired
    private TokenHandler tokenHandler;
            
     private static final Logger log = LoggerFactory.getLogger(SignUpController.class);
         
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createUser(@RequestBody UserForm form) {
        //Se existe um usuário com o mesmo username, não pode criar
        log.info("Requisição de novo usuario com username: " + form.getUsername());

            User u = new User();
            u.setUsername(form.getUsername());

            
            //Salve o password criptografado no banco
            u.setPassword(new BCryptPasswordEncoder().encode(form.getPassword()));
           u.setRole("author");
           System.out.println("blab  " + u.getPassword());
           
            
            userService.save(u);

        
        SecurityUser securityUser = new SecurityUser(u);
        String token = tokenHandler.createTokenForUser(securityUser);
        
        //Necessário para criar um formato válido de token para o ionic ler
        String jsonResponse ="{ \"token\":" + "\""+ token + "\"}";  
        return new ResponseEntity <> (jsonResponse, HttpStatus.OK);
    }
}

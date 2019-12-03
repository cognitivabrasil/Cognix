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

import com.cognitivabrasil.cognix.services.UserService;
import com.cognitivabrasil.cognix.web.security.CustomUserDetailsService;
import com.cognitivabrasil.cognix.web.security.LoginUser;
import com.cognitivabrasil.cognix.web.security.SecurityUser;
import com.cognitivabrasil.cognix.web.security.TokenHandler;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author igor
 */
@RestController

public class LoginController {

    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService service;

    @Autowired
    private CustomUserDetailsService customUserDetailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenHandler tokenHandler;
    

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createAuthenticationToken(@RequestBody LoginUser authenticationRequest) throws Exception {
        SecurityUser securityUser = new SecurityUser();
        securityUser = (SecurityUser) customUserDetailService.loadUserByUsername(authenticationRequest.getUsername());
        
        //Se não encontrou o usuário, então diz que noem de usuário não foi encontrado
        if(securityUser == null){
            String jsonResponse ="{ \"error\":" + "\""+ "userNotFound" + "\"}";
            return new ResponseEntity <> (jsonResponse, HttpStatus.OK);
        }else {
            
            try {
                authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            } catch(Exception e) {
                //Se achou o nome do usuário, mas o password está errado, dispara essa mensagem
                String jsonResponse ="{ \"error\":" + "\""+ "wrongPassword" + "\"}";
                return new ResponseEntity <> (jsonResponse, HttpStatus.OK);
            } 
            
        //Se encontrou, retorna um token válido para o frontend autenticar
        String token = tokenHandler.createTokenForUser(securityUser);
        //Necessário para criar um formato válido de token para o ionic ler
        String jsonResponse ="{ \"token\":" + "\""+ token + "\"}"; 
        return new ResponseEntity <> (jsonResponse, HttpStatus.OK);
        }
        
    }

    private void authenticate(String username, String password) throws Exception {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            throw new Exception("Deu probleminha  T.T ", e);
        }
    }
}

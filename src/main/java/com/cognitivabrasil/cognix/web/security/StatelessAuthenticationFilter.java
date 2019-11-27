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

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author igor
 */
@Component
@Import(TokenAuthenticationService.class)
public class StatelessAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private TokenAuthenticationService authenticationService;

    private final Logger log = LoggerFactory.getLogger(StatelessAuthenticationFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse response = (HttpServletResponse) res;




//        if (!httpRequest.getMethod().equals("OPTIONS")) {
            
            try {
                Authentication authentication = authenticationService.getAuthentication(httpRequest);
//                log.info(authentication.getName());
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                filterChain.doFilter(request, res);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException ex) {
                log.info("O token recebido Está expirado!", ex);
                SecurityContextHolder.getContext().setAuthentication(null);
                response.sendError(440, "Token expired. " + ex.getMessage());
                return;
            } catch (SignatureException | MalformedJwtException se) {
                log.info("O token recebido é inválido!", se);
                SecurityContextHolder.getContext().setAuthentication(null);
                response.sendError(HttpStatus.UNAUTHORIZED.value(), se.getMessage());
                return;
            }

            filterChain.doFilter(request, res);
//            SecurityContextHolder.getContext().setAuthentication(null);
        }
        //filterChain.doFilter(request, res);
       // SecurityContextHolder.getContext().setAuthentication(null);
    }

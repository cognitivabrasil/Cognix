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

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author igor
 */
public class CstFilter implements Filter {

    @Override
    public void destroy() {
        // Is not necessary destroy the token because is not saved, it is saved on frontend.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if ("OPTIONS".equals(httpRequest.getMethod())) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(new JsonConvertFilter(httpRequest), response);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        //It is not necessary. It is implemented because the interface needs.
    }

}

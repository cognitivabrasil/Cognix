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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author igor
 */
public class JsonConvertFilter extends HttpServletRequestWrapper {

    private final Logger LOG = LoggerFactory.getLogger(JsonConvertFilter.class);

    private final UserDetails userDetails;

    public JsonConvertFilter(HttpServletRequest request) {
        super(request);
        userDetails = getJson();
    }

    @Override
    public String getParameter(String key) {

        if (userDetails != null) {
            if ("username".equals(key)) {
                return userDetails.getUsername();
            }
            if ("password".equals(key)) {
                return userDetails.getPassword();
            }
        }
        LOG.debug("Called wrapper");
        return super.getParameter(key);
    }

    private UserDetails getJson() {

        try {
            final List<String> data = IOUtils.readLines(super.getReader());
            final String jsonData = data.stream().collect(Collectors.joining());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonData, SecurityUser.class);
        } catch (IOException e) {
            LOG.warn("Failed to read data {}", e.getMessage(), e);
            return null;
        }

    }

}

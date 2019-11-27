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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author igor
 */
public class SecurityUser extends LoginUser implements UserDetails {

    private boolean enabled = true;
    private List<String> roles;

    private final Logger log = LoggerFactory.getLogger(SecurityUser.class);

    public SecurityUser() {
    }

    public SecurityUser(User user) {
//        log.info("entrou no metodo");
//        log.info(user.getEmail());
        super(user.getEmail(), user.getPassword());
       // log.info(user.getUsername());
        this.roles = user.getRolesString();
        this.enabled = true;
    }

    public SecurityUser(String email, List<String> roles) {
        super(email, null);
        this.roles = roles;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        for (String s : this.roles) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(s);
            authorities.add(authority);

        }
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.enabled;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String getUsername() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

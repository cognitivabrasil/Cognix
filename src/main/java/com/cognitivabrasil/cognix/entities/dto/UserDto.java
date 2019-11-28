/*
 * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 * contato@cognitivabrasil.com.br for information.
 *
 */
package com.cognitivabrasil.cognix.entities.dto;

import com.cognitivabrasil.cognix.entities.User;
import com.cognitivabrasil.cognix.entities.validators.PasswordsEqualConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 * @author marcos
 */
@PasswordsEqualConstraint(message = "passwords are not equal")
public class UserDto {

    @NotEmpty
    private String username;
    @NotEmpty
    private String name;
    @NotEmpty
    private String role;
    @NotEmpty(message = "Informar uma senha com no mínimo 5 caracteres")
    @Size(min = 5, message = "A senha tem que ter no mínimo 5 caracteres")
    private String password;
    @NotEmpty(message = "Necessário confirmar a senha")
    @Size(min = 5, message = "A senha tem que ter no mínimo 5 caracteres")
    private String confirmPass;
    private Integer id;

    public UserDto() {
    }

    public UserDto(String username, String nome, String password, int id, String role) {
        this.username = username;
        this.name = nome;
        this.password = password;
        this.id = id;
        this.role = role;
    }

    public UserDto(User u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.name = u.getName();
        this.role = u.getRole();
        this.password = u.getPassword();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String nome) {
        this.name = nome.replaceAll("\\+", " ");
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }

    public User updateUser(User u) {
        if (u == null) {
            u = new User();
        }
        u.setName(name);
        u.setUsername(username);
        u.setRole(role);
        if (!isBlank(password)) {
            u.setPassword(password);
        }
        return u;
    }

    public User toUser() {
        User u = new User();
        return updateUser(u);
    }
}

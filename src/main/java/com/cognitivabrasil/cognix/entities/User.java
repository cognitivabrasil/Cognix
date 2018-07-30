/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/

package com.cognitivabrasil.cognix.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "users")
public class User {// implements UserDetails {

    public static final String MANAGE_DOC = "PERM_MANAGE_DOC";
    public static final String VIEW = "PERM_VIEW";
    public static final String MANAGE_USER = "PERM_MANAGE_USERS";
    public static final String CREATE_DOC = "PERM_CREATE_DOC";
    public static final String ROLE_DOC_ADMIN = "docadmin";
    public static final String ROLE_AUTHOR = "author";
    public static final String ROLE_VIEW = "view";
    public static final String ROLE_ROOT = "root";
    public static final int HASH_NUMBER = 7;

    private Integer id;
    private String login;
    private String passwordMd5;
    private String name;
    /* Internal representation of permission, as a string separated bu commas */
    private String permissionsInternal;
    private String role;
    private static final Map<String, String> ROLES;
    private Boolean deleted;
    private static final Map<String, String> PERMISSION_BY_ROLE;

    static {
        SortedMap<String, String> myRoles = new TreeMap<>();
        myRoles.put(ROLE_DOC_ADMIN, "Administrador de documentos");
        myRoles.put(ROLE_AUTHOR, "Criador de documentos");
        myRoles.put(ROLE_VIEW, "Somente visualizar");
        myRoles.put(ROLE_ROOT, "Superusu\u00e1rio");
        ROLES = Collections.unmodifiableSortedMap(myRoles);
    }

    static{
        PERMISSION_BY_ROLE = new HashMap<>();
        PERMISSION_BY_ROLE.put(ROLE_ROOT,
                User.MANAGE_USER + "," + User.VIEW + "," + User.MANAGE_DOC + "," + User.CREATE_DOC);
        PERMISSION_BY_ROLE.put(ROLE_DOC_ADMIN,
                User.VIEW + "," + User.MANAGE_DOC + "," + User.CREATE_DOC);
        PERMISSION_BY_ROLE.put(ROLE_AUTHOR,User.CREATE_DOC);
        PERMISSION_BY_ROLE.put(ROLE_VIEW,User.VIEW);
    }

    public User() {
        login = "";
        passwordMd5 = ""; // sonar: Credentials should not be hard-coded.
        name = "";
        permissionsInternal = "";
        role = "";
        deleted = false;
    }

    public User(String login, String name) {
        this.login = login;
        this.name = name;
    }


    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "login")
    public String getUsername() {
        return login;
    }

    /**
     * Sets the login.
     *
     * @param login the new login
     */
    public void setUsername(String login) {
        this.login = login;
    }

    /**
     *
     * @return User name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the User Name
     *
     * @param nome User name
     */
    public void setName(String nome) {
        this.name = nome;
    }

    /**
     * @return the passwordMd5
     */
    @Column(name = "password")
    protected String getPasswordMd5() {
        return passwordMd5;
    }

    /**
     * @param passwordMd5 the passwordMd5 to set
     */
    protected void setPasswordMd5(String passwordMd5) {
        this.passwordMd5 = passwordMd5;
    }


    /**
     * @return the permissionsInternal
     */
    @Column(name = "permissions")
    protected String getPermissionsInternal() {
        return permissionsInternal;
    }

    /**
     * @param permissionsInternal the permissionsInternal to set
     */
    private void setPermissionsInternal(String permissionsInternal) {
        this.permissionsInternal = permissionsInternal;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
        setPermissionsInternal(getPermissions(role));
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Gets the permissions by the role.
     *
     * @param role the role
     * @return the permissions
     */
    private String getPermissions(String role) {
        Map<String, String> roles = new HashMap<>();
        roles.put(ROLE_ROOT,
                User.MANAGE_USER + "," + User.VIEW + "," + User.MANAGE_DOC + "," + User.CREATE_DOC);
        roles.put(ROLE_DOC_ADMIN,
                User.VIEW + "," + User.MANAGE_DOC + "," + User.CREATE_DOC);
        roles.put(ROLE_AUTHOR,
                User.CREATE_DOC);
        roles.put(ROLE_VIEW,
                User.VIEW);
        return PERMISSION_BY_ROLE.get(role);

    }

    public boolean hasPermission(String permission){
        String permissions = getPermissions(getRole());
        List<String> roles = Arrays.asList(permissions.split(","));
        return roles.contains(permission);
    }

    /**
     * Checks if the user is root.
     *
     * @return Return true if the user is root and false otherwise
     */
    @Transient
    public boolean isRoot() {
        return role.equalsIgnoreCase(ROLE_ROOT);
    }

    /**
     * Reference data for the PERMISSION_BY_ROLE.
     *
     * @return the map
     */
    public static Map<String, String> getRoles() {
        return ROLES;
    }

    /**
     * Gets the name of role
     *
     * @return
     */
    @Transient
    public String getRoleNameText() {
        return User.getRoles().get(role);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final User other = (User) obj;

        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        else if (!Objects.equals(this.login, other.login)) {
            return false;
        }
        else if (!Objects.equals(this.passwordMd5, other.passwordMd5)) {
            return false;
        }
        else if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        else if (!Objects.equals(this.permissionsInternal, other.permissionsInternal)) {
            return false;
        }
        else if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = HASH_NUMBER;
        int eleven = 11;
        hash = eleven * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = eleven * hash + (this.login != null ? this.login.hashCode() : 0);
        hash = eleven * hash + (this.passwordMd5 != null ? this.passwordMd5.hashCode() : 0);
        hash = eleven * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = eleven * hash + (this.permissionsInternal != null ? this.permissionsInternal.hashCode() : 0);
        hash = eleven * hash + (this.role != null ? this.role.hashCode() : 0);
        return hash;
    }

}

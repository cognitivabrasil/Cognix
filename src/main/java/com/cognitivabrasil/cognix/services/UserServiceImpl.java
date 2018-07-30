/*
 * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 * contato@cognitivabrasil.com.br for information.
 */

package com.cognitivabrasil.cognix.services;

import com.cognitivabrasil.cognix.entities.User;
import com.cognitivabrasil.cognix.repositories.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@Service
public class UserServiceImpl implements UserService {

    private final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRep;

//    @Autowired
//    private DocumentRepository docRep;

    @Override
    public User get(String login) {
        return userRep.findByUsername(login);
    }

    @Override
    public User get(int id) {
        return userRep.getOne(id);
    }

    @Override
    public List<User> getAll() {
        return userRep.findByDeletedIsFalse();
    }

    @Override
    public void save(User u) {
        userRep.save(u);
    }

    @Override
    public void delete(User u) {
        if (u == null) {
            throw new DataAccessException("This user can not be null") {
            };
        }
        if (isLastAdmin(u)) {
            throw new IllegalStateException("Não é permitido deletar o último administrador do sistema.");
        }
        //testa se possui algum documento
        if (hasDocument(u)) {
            u.setDeleted(true);
            userRep.save(u);
        } else {
            userRep.delete(u);
        }

    }


    @Override
    public boolean hasDocument(User u) {
        return true;
//        return docRep.countByOwnerAndDeletedIsFalseAndActiveIsTrue(u) > 0;
    }

    @Override
    public List<User> getDeleted() {
        return userRep.findByDeletedIsTrue();
    }

    @Override
    public void activate(User u) {
        if (u == null) {
            throw new DataAccessException("This user can not be null") {
            };
        }
        u.setDeleted(false);
        userRep.save(u);
    }

    @Override
    public boolean isLastAdmin(User u) {
        if (!u.isRoot()) {
            return false;
        }
        return userRep.countByRole(User.ROLE_ROOT) < 2;
    }
}

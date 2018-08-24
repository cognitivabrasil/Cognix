/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/
package com.cognitivabrasil.cognix.services;

import com.cognitivabrasil.cognix.entities.Files;
import com.cognitivabrasil.cognix.repositories.FileRepository;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marcos Freitas Nunes <marcosn@gmail.com>
 */
@Service
public class FileServiceImpl implements FileService {

    private final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);
    @Autowired
    private FileRepository fileRep;

    /* (non-Javadoc)
     * @see cognitivabrasil.repositorio.models.AbstractServiceImpl#delete(java.lang.Object)
     */
    @Override
    public void deleteFile(Files file) throws IOException {
        try {
            file.deleteFile();
        } catch (IOException e) {
            LOG.error("Ao tentar deletar, não foi possível encontrar o arquivo: " + file.getLocation(), e);
            throw e;
        } finally {
            fileRep.delete(file);
        }
    }

    @Override
    public void save(Files f) {
        fileRep.save(f);
    }

    @Override
    public Files get(int id) {
        return fileRep.getOne(id);
    }

    @Override
    public List<Files> getAll() {
        return fileRep.findAll();
    }

}

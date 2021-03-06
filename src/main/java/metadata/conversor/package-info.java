
/*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/


/** 
 * Provê a funcionalidade necessária para converter de um objeto de metadados
 * para outro objeto de metadados.
 * 
 * <p>Ao importarmos dados de repositórios diversos, os dados nem sempre estão no formato
 * OBAA. O objetivo deste pacote é facilitar a conversão dos métadados em uma maneira
 * orientada à objetos. Para isso criamos um objeto {@link Conversor}, que contem
 * várias regras ({@link metadata.conversor.Rule}). Chamamos o método {@link Conversor.convert()} com
 * os objetos de origem e destino para realizar a conversão.</p>
 * 
 * <p>Exemplo: </p>
 * 
 * <pre>
 * {@code
 * 
 * c = new Conversor();
 * c.add(new Rule("Creator", "Author"));
 * c.add(new Rule("Title", "Title"));
 * 
 * c.convert(dublin, obaa);
 * }
 * </pre>
 * 
 * O código acima vai executar as seguintas chamadas de 
 * função nos objetos dublin e obaa:
 * 
 * <pre>
 * {@code obaa.setAuthor(dublin.getCreator());
 * obaa.setTitle(dublin.getTitle());}
 * </pre>
 *
 * 
 */
package metadata.conversor;

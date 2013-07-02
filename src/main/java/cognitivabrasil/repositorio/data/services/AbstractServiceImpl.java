/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.data.services;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class AbstractServiceImpl.
 * 
 * Use it to create concrete DAO classes.
 *
 * @param <T> the generic type
 * @author Paulo Schreiner <paulo@jorjao81.com>
 */
public abstract class AbstractServiceImpl<T> {

	/** The session factory. */
	@Autowired
	SessionFactory sessionFactory;
	
	/** The type. */
	private Class<T> type;

	/**
	 * Instantiates a new abstract ServiceImpl. 
	 * We need to store the class type, so we can execute Hibernate
	 * Queries for the correct entity, so we do it here.
	 */
	@SuppressWarnings("unchecked")
	public AbstractServiceImpl() {
		this.type = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Gets an entity by ID.
	 *
	 * @param id the id
	 * @return the entity of type T with the specified id.
	 */
	@SuppressWarnings("unchecked")
	public T get(int id) {
		return (T) this.sessionFactory.getCurrentSession().get(type, id);
	}

	/**
	 * Delete an entity from the database.
	 *
	 * @param item the item do be deleted
	 */
	public void delete(T item) {
		this.sessionFactory.getCurrentSession().delete(item);
		this.sessionFactory.getCurrentSession().flush();

	}

	/**
	 * Persists the entity to the database. If it already exists, it updates it.
	 *
	 * @param item the item do be saved
	 */
	public void save(T item) {
		Logger  log = Logger.getLogger(type.getName());
		log.info("Saving..." + item.toString());

		this.sessionFactory.getCurrentSession().saveOrUpdate(item);
		this.sessionFactory.getCurrentSession().flush();
	}

	/**
	 * Gets all entities of type T. Use with care for big collections.
	 *
	 * @return list of all the entities of type T
	 */
	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		return this.sessionFactory.getCurrentSession().createCriteria(type)
				.list();
	}
}
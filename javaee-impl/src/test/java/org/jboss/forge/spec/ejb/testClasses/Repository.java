package org.jboss.forge.spec.ejb.testClasses;

import java.util.List;

/**
 * @author fiorenzo pizza
 * 
 * @param <T>
 */
public interface Repository<T> {

	/**
	 * @return
	 */
	public List<T> getAllList();

	/**
	 * Find by primary key
	 * 
	 * @param key
	 * @return
	 */
	public T find(Object key);

	/**
	 * Fetch by primary key
	 * 
	 * @param key
	 * @return
	 */
	public T fetch(Object key);

	/**
	 * Make an instance persistent.
	 * <p>
	 * 
	 * @param object
	 * @return
	 */
	public T persist(T object);

	/**
	 * @param object
	 * @return
	 */
	public boolean update(T object);

	/**
	 * @param key
	 * @return
	 */
	public boolean delete(Object key);

}

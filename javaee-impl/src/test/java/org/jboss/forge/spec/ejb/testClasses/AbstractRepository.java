package org.jboss.forge.spec.ejb.testClasses;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

/**
 * @author fiorenzo pizza
 */
public abstract class AbstractRepository<T> implements Serializable,
		Repository<T> {

	private static final long serialVersionUID = 1L;

	protected abstract EntityManager getEm();

	public abstract void setEm(EntityManager em);

	protected static final Logger logger = Logger
			.getLogger(AbstractRepository.class.getName());

	@SuppressWarnings("unchecked")
	protected Class<T> getEntityType() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass()
				.getGenericSuperclass();
		return (Class<T>) parameterizedType.getActualTypeArguments()[0];
	}

	public T find(Object key) {
		try {
			return getEm().find(getEntityType(), key);
		} catch (Exception e) {
			logger.log(Level.SEVERE, null, e);
			return null;
		}
	}

	public boolean update(T object) {
		try {
			getEm().merge(object);
			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, null, e);
			return false;
		}
	}

	public boolean delete(Object key) {
		try {
			T obj = getEm().find(getEntityType(), key);
			if (obj != null) {
				getEm().remove(obj);
				// getEm().flush();
			}
			return true;
		} catch (Exception e) {
			logger.log(Level.SEVERE, null, e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> getAllList() {
		try {
			CriteriaQuery<T> criteriaQuery = (CriteriaQuery<T>) getEm()
					.getCriteriaBuilder().createQuery();
			criteriaQuery.select(criteriaQuery.from(getEntityType()));
			return getEm().createQuery(criteriaQuery).getResultList();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, null, ex);
			return new ArrayList<T>();
		}
	}

}

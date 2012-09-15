package org.jboss.forge.spec.ejb.testClasses;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * BaseRepository
 * 
 * @param <T>
 */
public abstract class BaseRepository<T> extends AbstractRepository<T> {

	private static final long serialVersionUID = 1L;

	// --- JPA ---------------------------------

	@PersistenceContext(unitName = "PU")
	protected EntityManager em;

	@Override
	protected EntityManager getEm() {
		return em;
	}

	@Override
	public void setEm(EntityManager em) {
		this.em = em;
	}

}

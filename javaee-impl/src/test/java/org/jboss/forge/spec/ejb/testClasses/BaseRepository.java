package org.jboss.forge.spec.ejb.testClasses;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author fiorenzo pizza
 */
public abstract class BaseRepository<T> extends AbstractRepository<T> {

	private static final long serialVersionUID = 1L;

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

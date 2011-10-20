package org.jboss.forge.scaffold.metawidget.persistence;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */

public class PersistenceUtil implements Serializable
{
   private static final long serialVersionUID = -276417828563635020L;

   @Inject
   protected EntityManager entityManager;

   public <T> int count(Class<T> type)
   {
      EntityManager em = getEntityManager();
      CriteriaQuery<Long> cq = em.getCriteriaBuilder().createQuery(Long.class);
      javax.persistence.criteria.Root<T> rt = cq.from(type);
      cq.select(em.getCriteriaBuilder().count(rt));
      javax.persistence.Query q = em.createQuery(cq);
      return ((Long) q.getSingleResult()).intValue();
   }

   protected <T> void create(final T entity)
   {
      EntityManager em = getEntityManager();
      em.joinTransaction();
      em.persist(entity);
   }

   protected <T> void delete(final T entity) throws NoResultException
   {
      EntityManager em = getEntityManager();
      em.joinTransaction();
      em.remove(entity);
   }

   protected <T> T deleteById(final Class<T> type, final Long id) throws NoResultException
   {
      T object = findById(type, id);
      delete(object);
      return object;
   }

   protected <T> List<T> findAll(final Class<T> type)
   {
      EntityManager em = getEntityManager();
      CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(type);
      query.from(type);
      return em.createQuery(query).getResultList();
   }

   @SuppressWarnings("unchecked")
   public <T> List<T> findAll(Class<T> type, int firstResult, int maxResults)
   {
      EntityManager em = getEntityManager();
      CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(type);
      cq.select(cq.from(type));
      javax.persistence.Query q = em.createQuery(cq);
      q.setMaxResults(maxResults);
      q.setFirstResult(firstResult);
      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   protected <T> T findById(final Class<T> type, final Long id) throws NoResultException
   {
      Class<?> clazz = getObjectClass(type);
      EntityManager em = getEntityManager();
      T result = (T) em.find(clazz, id);
      if (result == null)
      {
         throw new NoResultException("No object of type: " + type + " with ID: " + id);
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   protected <T> List<T> findByNamedQuery(final String namedQueryName)
   {
      EntityManager em = getEntityManager();
      return em.createNamedQuery(namedQueryName).getResultList();
   }

   @SuppressWarnings("unchecked")
   protected <T> List<T> findByNamedQuery(final String namedQueryName, final Object... params)
   {
      EntityManager em = getEntityManager();
      Query query = em.createNamedQuery(namedQueryName);
      int i = 1;
      for (Object p : params)
      {
         query.setParameter(i++, p);
      }
      return query.getResultList();
   }

   @SuppressWarnings("unchecked")
   protected <T> T findUniqueByNamedQuery(final String namedQueryName) throws NoResultException
   {
      EntityManager em = getEntityManager();
      return (T) em.createNamedQuery(namedQueryName).getSingleResult();
   }

   @SuppressWarnings("unchecked")
   protected <T> T findUniqueByNamedQuery(final String namedQueryName, final Object... params) throws NoResultException
   {
      EntityManager em = getEntityManager();
      Query query = em.createNamedQuery(namedQueryName);
      int i = 1;
      for (Object p : params)
      {
         query.setParameter(i++, p);
      }
      return (T) query.getSingleResult();
   }

   protected EntityManager getEntityManager()
   {
      return entityManager;
   }

   protected Class<?> getObjectClass(final Object type) throws IllegalArgumentException
   {
      Class<?> clazz = null;
      if (type == null)
      {
         throw new IllegalArgumentException("Null has no type. You must pass an Object");
      }
      else if (type instanceof Class<?>)
      {
         clazz = (Class<?>) type;
      }
      else
      {
         clazz = type.getClass();
      }
      return clazz;
   }

   protected <T> void refresh(final T entity)
   {
      getEntityManager().refresh(entity);
   }

   protected <T> void save(final T entity)
   {
      EntityManager em = getEntityManager();
      em.joinTransaction();
      em.merge(entity);
   }
}

package org.jboss.forge.spec.ejb.testClasses;

import java.util.List;

/**
 * @author fiorenzo pizza
 */
public interface Repository<T>
{

   public List<T> getAllList();

   public T find(Object key);

   public T fetch(Object key);

   public T persist(T object);

   public boolean update(T object);

   public boolean delete(Object key);

}

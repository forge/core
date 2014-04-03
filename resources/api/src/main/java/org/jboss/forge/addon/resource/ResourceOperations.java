package org.jboss.forge.addon.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Resource Operations
 *
 * @author Shane Bryzak
 */
public interface ResourceOperations<T>
{
   public boolean resourceExists(T resource);

   public boolean resourceExistsAndIsDirectory(T resource);

   public T[] listResources(T resource);

   public long getResourceLength(T resource);

   public boolean renameResource(T src, T dest);

   public void copyResource(T src, T dest) throws IOException;

   public boolean deleteResource(T resource);

   public void deleteResourceOnExit(T resource);

   public boolean createNewResource(T resource) throws IOException;

   public boolean mkdir(T resource);

   public boolean mkdirs(T resource);

   public OutputStream createOutputStream(T resource) throws IOException;

   public InputStream createInputStream(T resource) throws IOException;
}

package org.jboss.forge.addon.resource;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Resource Operations
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author Shane Bryzak
 * @author <a href="lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ResourceOperations<T>
{
   boolean exists(T resource);

   boolean existsAndIsDirectory(T resource);

   T[] listChildren(T resource);

   long getLength(T resource);

   boolean rename(T src, T dest);

   void copy(T src, T dest) throws ResourceException;

   boolean delete(T resource);

   void deleteOnExit(T resource);

   boolean create(T resource) throws ResourceException;

   boolean mkdir(T resource) throws ResourceException;

   boolean mkdirs(T resource) throws ResourceException;

   OutputStream createOutputStream(T resource) throws ResourceException;

   InputStream createInputStream(T resource) throws ResourceException;

   long getLastModifiedTime(T resource);
}

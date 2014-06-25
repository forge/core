package org.jboss.forge.addon.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation for {@link Path} based {@link ResourceOperations} interface
 *
 * @author Shane Bryzak
 *
 */
public enum DefaultPathOperations implements ResourceOperations<Path>
{
   INSTANCE;

   @Override
   public boolean exists(Path resource)
   {
      return Files.exists(resource);
   }

   @Override
   public boolean existsAndIsDirectory(Path resource)
   {
      return Files.isDirectory(resource);
   }

   @Override
   public Path[] listChildren(Path resource)
   {
      List<Path> resources = new ArrayList<Path>();

      DirectoryStream<Path> stream;
      try
      {
         stream = Files.newDirectoryStream(resource, new DirectoryStream.Filter<Path>()
         {
            @Override
            public boolean accept(Path entry) throws IOException
            {
               return Files.isDirectory(entry);
            }
         });
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex.getMessage(), ex);
      }

      for (Path entry : stream)
      {
         resources.add(entry);
      }

      return resources.toArray(new Path[resources.size()]);
   }

   @Override
   public long getLength(Path resource)
   {
      if (Files.isDirectory(resource))
      {
         throw new UnsupportedOperationException();
      }

      try
      {
         return Files.size(resource);
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex.getMessage(), ex);
      }
   }

   @Override
   public boolean rename(Path src, Path dest)
   {
      try
      {
         return Files.move(src, dest) != null;
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex.getMessage(), ex);
      }
   }

   @Override
   public void copy(Path src, Path dest) throws ResourceException
   {
      try
      {
         Files.copy(src, dest);
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   public boolean delete(Path resource)
   {
      try
      {
         Files.delete(resource);
         return true;
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex.getMessage(), ex);
      }
   }

   @Override
   public void deleteOnExit(Path resource)
   {
      resource.toFile().deleteOnExit();
   }

   @Override
   public boolean create(Path resource) throws ResourceException
   {
      try
      {
         return Files.createFile(resource) != null;
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   public boolean mkdir(Path resource) throws ResourceException
   {
      try
      {
         return Files.createDirectory(resource) != null;
      }
      catch (FileAlreadyExistsException ex)
      {
         return false;
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex.getMessage(), ex);
      }
   }

   @Override
   public boolean mkdirs(Path resource) throws ResourceException
   {
      try
      {
         return Files.createDirectories(resource) != null;
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex.getMessage(), ex);
      }
   }

   @Override
   public OutputStream createOutputStream(Path resource) throws ResourceException
   {
      try
      {
         return Files.newOutputStream(resource);
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   public InputStream createInputStream(Path resource) throws ResourceException
   {
      try
      {
         return Files.newInputStream(resource);
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   public long getLastModifiedTime(Path resource)
   {
      try
      {
         return Files.getLastModifiedTime(resource).toMillis();
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }
}

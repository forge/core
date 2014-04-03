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
   public boolean resourceExists(Path resource)
   {
      return Files.exists(resource);
   }

   @Override
   public boolean resourceExistsAndIsDirectory(Path resource)
   {
      return Files.isDirectory(resource);
   }

   @Override
   public Path[] listResources(Path resource)
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
         throw new ResourceException(ex);
      }

      for (Path entry : stream)
      {
         resources.add(entry);
      }

      return resources.toArray(new Path[resources.size()]);
   }

   @Override
   public long getResourceLength(Path resource)
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
         throw new ResourceException(ex);
      }
   }

   @Override
   public boolean renameResource(Path src, Path dest)
   {
      try {
         return Files.move(src, dest) != null;
     } catch (IOException ex) {
         throw new ResourceException(ex);
     }
   }

   @Override
   public void copyResource(Path src, Path dest) throws IOException
   {
      Files.copy(src, dest);
   }

   @Override
   public boolean deleteResource(Path resource)
   {
      try
      {
         Files.delete(resource);
         return true;
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex);
      }
   }

   @Override
   public void deleteResourceOnExit(Path resource)
   {
      resource.toFile().deleteOnExit();
   }

   @Override
   public boolean createNewResource(Path resource) throws IOException
   {
      return Files.createFile(resource) != null;
   }

   @Override
   public boolean mkdir(Path resource)
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
         throw new ResourceException(ex);
      }
   }

   @Override
   public boolean mkdirs(Path resource)
   {
      try
      {
         return Files.createDirectories(resource) != null;
      }
      catch (IOException ex)
      {
         throw new ResourceException(ex);
      }
   }

   @Override
   public OutputStream createOutputStream(Path resource) throws IOException
   {
      return  Files.newOutputStream(resource);
   }

   @Override
   public InputStream createInputStream(Path resource) throws IOException
   {
      return Files.newInputStream(resource);
   }
}

package org.jboss.forge.addon.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Streams;

/**
 * PathResource implementation
 *
 * @author Shane Bryzak
 *
 */
public class PathResourceImpl extends AbstractResource<Path> implements PathResource
{
   private volatile List<Resource<?>> listCache;
   protected Path path;
   protected long lastModification;

   protected PathResourceImpl(ResourceFactory factory, Path file) throws IOException
   {
      super(factory, null);

      if ((this.path = file) != null)
      {
         try
         {
            this.lastModification = getPathOperations().getLastModifiedTime(path);
         }
         catch (Exception ex)
         {
            this.lastModification = 0L;
         }
      }
   }

   @Override
   public boolean create()
   {
      return createNewPath();
   }
   
   @Override
   public String getName()
   {
      return path.getFileName().toString();
   }

   @Override
   public String toString()
   {
      return getFullyQualifiedName();
   }

   @Override
   public Path getUnderlyingResourceObject()
   {
      return path;
   }

   @Override
   public InputStream getResourceInputStream()
   {
      return getPathOperations().createInputStream(path);
   }

   @Override
   public PathResource getParent()
   {
      return path.getParent() != null ? getResourceFactory().create(PathResource.class, path.getParent()) : null;
   }

   @Override
   public Resource<?> getChild(final String name)
   {
      return getResourceFactory().create(path.resolve(name));
   }

   @Override
   public boolean exists()
   {
      return getPathOperations().exists(path);
   }

   @Override
   public boolean isDirectory()
   {
      return getPathOperations().existsAndIsDirectory(path);
   }

   @Override
   public boolean isStale()
   {
      return Long.compare(lastModification, getPathOperations().getLastModifiedTime(path)) != 0;
   }

   @Override
   public void refresh()
   {
      lastModification = getPathOperations().getLastModifiedTime(path);
   }

   @Override
   public boolean mkdir()
   {
      return getPathOperations().mkdir(path);
   }

   @Override
   public boolean mkdirs()
   {
      return getPathOperations().mkdirs(path);
   }

   @Override
   public boolean delete()
   {
      return getPathOperations().delete(path);
   }

   @Override
   public boolean delete(final boolean recursive)
   {
      if (recursive)
      {
         try
         {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
               @Override
               public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
               {
                  getPathOperations().delete(file);
                  return FileVisitResult.CONTINUE;
               }

               @Override
               public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException
               {
                  if (e == null)
                  {
                     getPathOperations().delete(dir);
                     return FileVisitResult.CONTINUE;
                  }
                  else
                  {
                     // directory iteration failed
                     throw e;
                  }
               }
            });
            return true;
         }
         catch (IOException ex)
         {
            throw new ResourceException(ex.getMessage(), ex);
         }
      }
      else
      {
         return delete();
      }
   }

   @Override
   public void deleteOnExit()
   {
      getPathOperations().deleteOnExit(path);
   }

   @Override
   public PathResource setContents(String data)
   {
      if (data == null)
      {
         data = "";
      }
      return setContents(data.toCharArray());
   }

   @Override
   public PathResource setContents(String data, Charset charset)
   {
      if (data == null)
      {
         data = "";
      }
      return setContents(data.toCharArray(), charset);
   }

   @Override
   public PathResource setContents(char[] data, Charset charset)
   {
      return setContents(new ByteArrayInputStream(new String(data).getBytes(charset)));
   }

   @Override
   public PathResource setContents(final char[] data)
   {
      return setContents(new ByteArrayInputStream(new String(data).getBytes()));
   }

   @Override
   public PathResource setContents(final InputStream data)
   {
      Assert.notNull(data, "InputStream must not be null.");

      try
      {
         if (!exists())
         {
            getParent().mkdirs();
            if (!createNewPath())
            {
               throw new ResourceException("Failed to create path: " + path);
            }
         }

         OutputStream out = getPathOperations().createOutputStream(path);
         try
         {
            Streams.write(data, out);
         }
         finally
         {
            Streams.closeQuietly(data);
            out.flush();
            Streams.closeQuietly(out);
            if (OperatingSystemUtils.isWindows())
            {
               System.gc();
            }
         }
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
      return this;
   }

   @Override
   public boolean createNewPath()
   {
      getParent().mkdirs();
      if (getPathOperations().create(path))
      {
         return true;
      }
      return false;
   }

   @Override
   public PathResource createTempResource()
   {
      try
      {
         PathResource result = createFrom(Files.createTempFile("forgetemp", ""));
         return result;
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   public boolean renameTo(final String pathspec)
   {
      return getPathOperations().rename(path, path.resolveSibling(pathspec));
   }

   @Override
   public boolean renameTo(final PathResource target)
   {
      if (getPathOperations().rename(path, target.getUnderlyingResourceObject()))
      {
         path = target.getUnderlyingResourceObject();
         return true;
      }
      else
      {
         return false;
      }
   }

   @Override
   public long getSize()
   {
      return getPathOperations().getLength(path);
   }

   @Override
   public boolean isExecutable()
   {
      return Files.isExecutable(path);
   }

   @Override
   public boolean isReadable()
   {
      return Files.isReadable(path);
   }

   @Override
   public boolean isWritable()
   {
      return Files.isWritable(path);
   }

   @Override
   public String getFullyQualifiedName()
   {
      return this.path.toAbsolutePath().toString();
   }

   @Override
   public ResourceMonitor monitor()
   {
      return getResourceFactory().monitor(this);
   }

   @Override
   public ResourceMonitor monitor(ResourceFilter filter)
   {
      return getResourceFactory().monitor(this, filter);
   }

   protected ResourceOperations<Path> getPathOperations()
   {
      return getResourceFactory().<Path> getResourceOperations(Path.class);
   }

   @Override
   public long getLastModified()
   {
      try
      {
         return Files.getLastModifiedTime(path).toMillis();
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   public void setLastModified(long time)
   {
      try
      {
         Files.setLastModifiedTime(path, FileTime.fromMillis(time));
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   public PathResource createFrom(Path path)
   {
      try
      {
         return new PathResourceImpl(getResourceFactory(), path);
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      if (isDirectory())
      {
         if (isStale())
         {
            listCache = null;
         }

         if (listCache == null)
         {
            refresh();
            listCache = new LinkedList<>();

            Path[] files = getPathOperations().listChildren(getUnderlyingResourceObject());
            if (files != null)
            {
               for (Path f : files)
               {
                  listCache.add(getResourceFactory().create(f));
               }
            }
         }

         return listCache;
      }
      else
      {
         return Collections.emptyList();
      }
   }

   @Override
   public boolean supports(ResourceFacet type)
   {
      return false;
   }

   @Override
   public OutputStream getResourceOutputStream()
   {
      return getPathOperations().createOutputStream(path);
   }
}

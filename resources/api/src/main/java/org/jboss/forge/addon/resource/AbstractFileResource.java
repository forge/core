package org.jboss.forge.addon.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Streams;

/**
 * A standard, built-in resource for representing files on the filesystem.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractFileResource<T extends FileResource<T>> extends AbstractResource<File> implements
         FileResource<T>
{
   private File file;
   private long lastModification = -1;

   protected AbstractFileResource(final ResourceFactory factory, final File file)
   {
      super(factory, null);
      this.file = file;
   }

   @Override
   public String getName()
   {
      return file.getName();
   }

   @Override
   public String toString()
   {
      return getFullyQualifiedName();
   }

   @Override
   public File getUnderlyingResourceObject()
   {
      return file;
   }

   @Override
   public InputStream getResourceInputStream()
   {
      try
      {
         return getFileOperations().createInputStream(file);
      }
      catch (IOException e)
      {
         throw new ResourceException("cannot obtain stream to file: file does not exist: " + file.getAbsolutePath());
      }
   }

   @Override
   public DirectoryResource getParent()
   {
      return file.getParentFile() != null ? getResourceFactory().create(DirectoryResource.class, file.getParentFile())
               : null;
   }

   @Override
   public Resource<?> getChild(final String name)
   {
      return null;
   }

   /**
    * Create a new {@link Resource} instance for the target file. The new {@link Resource} should be of the same type as
    * <b>this</b>.
    * 
    * @param file The file to create the resource instance from.
    * @return A new resource.
    */
   @Override
   public abstract Resource<File> createFrom(File file);

   @Override
   public boolean exists()
   {
      return getFileOperations().fileExists(file);
   }

   @Override
   public boolean isDirectory()
   {
      return getFileOperations().fileExistsAndIsDirectory(file);
   }

   @Override
   public boolean isStale()
   {
      return lastModification != getUnderlyingResourceObject().lastModified();
   }

   @Override
   public void refresh()
   {
      lastModification = getUnderlyingResourceObject().lastModified();
   }

   @Override
   public boolean mkdir()
   {
      return getFileOperations().mkdir(file);
   }

   @Override
   public boolean mkdirs()
   {
      return getFileOperations().mkdirs(file);
   }

   @Override
   public boolean delete()
   {
      return delete(false);
   }

   @Override
   public boolean delete(final boolean recursive)
   {
      if (recursive)
      {
         if (_deleteRecursive(file, true))
         {
            return true;
         }
         return false;
      }

      File[] listFiles = getFileOperations().listFiles(file);
      if ((listFiles != null) && (listFiles.length != 0))
      {
         throw new RuntimeException("directory not empty");
      }

      if (OperatingSystemUtils.isWindows())
      {
         System.gc(); // ensure no lingering handles that would prevent deletion
      }

      if (getFileOperations().deleteFile(file))
      {
         return true;
      }
      return false;
   }

   @Override
   public void deleteOnExit()
   {
      getFileOperations().deleteFileOnExit(file);
   }

   private boolean _deleteRecursive(final File file, final boolean collect)
   {
      if (collect && OperatingSystemUtils.isWindows())
      {
         System.gc(); // ensure no lingering handles that would prevent deletion
      }

      if (file == null)
      {
         return false;
      }

      File[] children = getFileOperations().listFiles(file);
      if (children != null)
      {
         for (File sf : children)
         {
            if (getFileOperations().fileExistsAndIsDirectory(sf))
            {
               _deleteRecursive(sf, false);
            }
            else
            {
               if (!getFileOperations().deleteFile(sf))
               {
                  throw new RuntimeException("failed to delete: " + sf.getAbsolutePath());
               }
            }
         }
      }

      return getFileOperations().deleteFile(file);
   }

   @Override
   public T setContents(String data)
   {
      if (data == null)
      {
         data = "";
      }
      return setContents(data.toCharArray());
   }

   @Override
   public T setContents(String data, Charset charset)
   {
      if (data == null)
      {
         data = "";
      }
      return setContents(data.toCharArray(), charset);
   }

   @Override
   public T setContents(char[] data, Charset charset)
   {
      return setContents(new ByteArrayInputStream(new String(data).getBytes(charset)));
   }

   @Override
   public T setContents(final char[] data)
   {
      return setContents(new ByteArrayInputStream(new String(data).getBytes()));
   }

   @Override
   @SuppressWarnings("unchecked")
   public T setContents(final InputStream data)
   {
      Assert.notNull(data, "InputStream must not be null.");

      try
      {
         if (!exists())
         {
            getParent().mkdirs();
            if (!createNewFile())
            {
               throw new IOException("Failed to create file: " + file);
            }
         }

         OutputStream out = getFileOperations().createOutputStream(file);
         try
         {
            Streams.write(data, out);
         }
         finally
         {
            Streams.closeQuietly(data);
            out.flush();
            Streams.closeQuietly(out);
         }
      }
      catch (IOException e)
      {
         throw new ResourceException("Error while setting the contents", e);
      }
      return (T) this;
   }

   @Override
   public boolean createNewFile()
   {
      try
      {
         getParent().mkdirs();
         if (getFileOperations().createNewFile(file))
         {
            return true;
         }
         return false;
      }
      catch (IOException e)
      {
         throw new ResourceException("Error while creating a new file", e);
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public T createTempResource()
   {
      try
      {
         T result = (T) createFrom(File.createTempFile("forgetemp", ""));
         return result;
      }
      catch (IOException e)
      {
         throw new ResourceException("Error while creating a temporary resource", e);
      }
   }

   @Override
   public boolean renameTo(final String pathspec)
   {
      return renameTo(new File(pathspec));
   }

   @Override
   public boolean renameTo(final FileResource<?> target)
   {
      return renameTo(target.getUnderlyingResourceObject());
   }

   private boolean renameTo(final File target)
   {
      if (getFileOperations().renameFile(file, target))
      {
         file = target;
         return true;
      }
      return false;
   }

   @Override
   public long getSize()
   {
      return getFileOperations().getFileLength(file);
   }

   @Override
   public boolean isExecutable()
   {
      return (this.file.canExecute() && !getFileOperations().fileExistsAndIsDirectory(file));
   }

   @Override
   public boolean isReadable()
   {
      return (this.file.canRead() && !getFileOperations().fileExistsAndIsDirectory(file));
   }

   @Override
   public boolean isWritable()
   {
      return (this.file.canWrite() && !getFileOperations().fileExistsAndIsDirectory(file));
   }

   @Override
   public String getFullyQualifiedName()
   {
      return this.file.getAbsolutePath();
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

   protected FileOperations getFileOperations()
   {
      return getResourceFactory().getFileOperations();
   }

   @Override
   public long getLastModified()
   {
      return file.lastModified();
   }

   @Override
   public void setLastModified(long time)
   {
      file.setLastModified(time);
   }

   @Override
   public OutputStream getResourceOutputStream()
   {
      try
      {
         return getFileOperations().createOutputStream(file);
      }
      catch (IOException ioe)
      {
         throw new ResourceException("Error while creating OutputStream for Resource " + this, ioe);
      }
   }
}
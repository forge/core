package org.jboss.forge.addon.resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
   protected File file;
   protected long lastModification;

   protected AbstractFileResource(final ResourceFactory factory, final File file)
   {
      super(factory, null);

      if ((this.file = file) != null)
      {
         this.lastModification = file.lastModified();
      }
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
         return new BufferedInputStream(new FileInputStream(file));
      }
      catch (FileNotFoundException e)
      {
         throw new ResourceException("cannot obtain stream to file: file does not exist: " + file.getAbsolutePath());
      }
   }

   @Override
   public DirectoryResource getParent()
   {
      return file.getParentFile() != null ? resourceFactory.create(DirectoryResource.class, file.getParentFile())
               : null;
   }

   @Override
   public Resource<?> getChild(final String name)
   {
      throw new ResourceException("[" + this.getClass().getSimpleName() + "] can have no children");
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
      return getUnderlyingResourceObject().exists();
   }

   @Override
   public boolean isDirectory()
   {
      return file.isDirectory();
   }

   @Override
   public boolean isStale()
   {
      return lastModification != getUnderlyingResourceObject().lastModified();
   }

   @Override
   public void markUpToDate()
   {
      lastModification = getUnderlyingResourceObject().lastModified();
   }

   @Override
   public boolean mkdir()
   {
      return file.mkdir();
   }

   @Override
   public boolean mkdirs()
   {
      return file.mkdirs();
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

      if ((file.listFiles() != null) && (file.listFiles().length != 0))
      {
         throw new RuntimeException("directory not empty");
      }

      if (OperatingSystemUtils.isWindows())
      {
         System.gc(); // ensure no lingering handles that would prevent deletion
      }

      if (file.delete())
      {
         return true;
      }
      return false;
   }

   @Override
   public void deleteOnExit()
   {
      file.deleteOnExit();
   }

   private static boolean _deleteRecursive(final File file, final boolean collect)
   {
      if (collect && OperatingSystemUtils.isWindows())
      {
         System.gc(); // ensure no lingering handles that would prevent deletion
      }

      if (file == null)
      {
         return false;
      }

      File[] children = file.listFiles();
      if (children != null)
      {
         for (File sf : children)
         {
            if (sf.isDirectory())
            {
               _deleteRecursive(sf, false);
            }
            else
            {
               if (!sf.delete())
               {
                  throw new RuntimeException("failed to delete: " + sf.getAbsolutePath());
               }
            }
         }
      }

      return file.delete();
   }

   @Override
   public String getContents()
   {
      InputStream stream = getResourceInputStream();
      try
      {
         return Streams.toString(stream);
      }
      finally
      {
         Streams.closeQuietly(stream);
      }
   }

   @Override
   public String getContents(Charset charset)
   {
      InputStream stream = getResourceInputStream();
      try
      {
         return Streams.toString(stream, charset);
      }
      finally
      {
         Streams.closeQuietly(stream);
      }
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
            mkdirs();
            delete();
            if (!createNewFile())
            {
               throw new IOException("Failed to create file: " + file);
            }
         }

         file.delete();

         OutputStream out = new FileOutputStream(file);
         try
         {
            byte buf[] = new byte[1024];
            int len;
            while ((len = data.read(buf)) > 0)
            {
               out.write(buf, 0, len);
            }
         }
         finally
         {
            if (data != null)
               data.close();

            out.flush();
            out.close();
            if (OperatingSystemUtils.isWindows())
            {
               System.gc();
            }
         }
      }
      catch (IOException e)
      {
         throw new ResourceException(e);
      }
      return (T) this;
   }

   @Override
   public boolean createNewFile()
   {
      try
      {
         if (file.mkdirs())
         {
            file.delete();
         }
         if (file.createNewFile())
         {
            return true;
         }
         return false;
      }
      catch (IOException e)
      {
         throw new ResourceException(e);
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
         throw new ResourceException(e);
      }
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public <R extends Resource<?>> R reify(final Class<R> type)
   {
      Resource<?> result = resourceFactory.create((Class) type, file);
      if (result != null && type.isAssignableFrom(result.getClass()))
      {
         return (R) result;
      }
      else
      {
         return null;
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
      if (file.renameTo(target))
      {
         file = target;
         return true;
      }
      return false;
   }

   @Override
   public long getSize()
   {
      return file.length();
   }

   @Override
   public boolean isExecutable()
   {
      return (this.file.canExecute() && !this.file.isDirectory());
   }

   @Override
   public boolean isReadable()
   {
      return (this.file.canRead() && !this.file.isDirectory());
   }

   @Override
   public boolean isWritable()
   {
      return (this.file.canWrite() && !this.file.isDirectory());
   }

   @Override
   public String getFullyQualifiedName()
   {
      return this.file.getAbsolutePath();
   }

   @Override
   public ResourceMonitor monitor()
   {
      return resourceFactory.monitor(this);
   }

   @Override
   public ResourceMonitor monitor(ResourceFilter filter)
   {
      return resourceFactory.monitor(this, filter);
   }
}
package org.jboss.forge.resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.OperatingSystemUtils;
import org.jboss.forge.resource.events.ResourceCreated;
import org.jboss.forge.resource.events.ResourceDeleted;
import org.jboss.forge.resource.events.ResourceModified;
import org.jboss.forge.resource.events.ResourceRenamed;
import org.jboss.forge.resource.events.TempResourceCreated;

/**
 * A standard, built-in resource for representing files on the filesystem.
 *
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

   /**
    * Get the actual underlying file resource that this resource instance represents, whether existing or non-existing.
    *
    * @return An instance of {@link File}
    */
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

   /**
    * Get the parent of the current resource. Returns null if the current resource is the project root.
    *
    * @return An instance of the resource parent.
    */
   @Override
   public Resource<?> getParent()
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

   /**
    * Return true if this {@link AbstractFileResource} exists and is actually a directory, otherwise return false;
    */
   @Override
   public boolean isDirectory()
   {
      return file.isDirectory();
   }

   /**
    * Returns true if the underlying resource has been modified on the file system since it was initially loaded.
    *
    * @return boolean true if resource is changed.
    */
   @Override
   public boolean isStale()
   {
      return lastModification != getUnderlyingResourceObject().lastModified();
   }

   /**
    * Re-read the last modified timestamp for this resource.
    */
   @Override
   public void markUpToDate()
   {
      lastModification = getUnderlyingResourceObject().lastModified();
   }

   /**
    * Create a new single directory for this resource. This will not succeed if any parent directories needed for this
    * resource to exist are missing. You should consider using {@link #mkdirs()}
    */
   @Override
   public boolean mkdir()
   {
      if (file.mkdir())
      {
         resourceFactory.fireEvent(new ResourceCreated(this));
         return true;
      }
      return false;
   }

   /**
    * Create all directories required for this resource to exist.
    */
   @Override
   public boolean mkdirs()
   {
      if (file.mkdirs())
      {
         resourceFactory.fireEvent(new ResourceCreated(this));
         return true;
      }
      return false;
   }

   /**
    * Delete this file, non-recursively.
    */
   @Override
   public boolean delete()
   {
      return delete(false);
   }

   /**
    * Delete this {@link Resource}, and all child resources.
    */
   @Override
   public boolean delete(final boolean recursive)
   {
      if (recursive)
      {
         if (_deleteRecursive(file, true))
         {
            resourceFactory.fireEvent(new ResourceDeleted(this));
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
         resourceFactory.fireEvent(new ResourceDeleted(this));
         return true;
      }
      return false;
   }

   /**
    * Requests that the file or directory denoted by this resource be deleted when the virtual machine terminates.
    * <p>
    * Once deletion has been requested, it is not possible to cancel the request. This method should therefore be used
    * with care.
    */
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

   /**
    * Set the contents of this {@link AbstractFileResource} to the given {@link String}
    */
   @Override
   public T setContents(String data)
   {
      if (data == null)
      {
         data = "";
      }
      return setContents(data.toCharArray());
   }

   /**
    * Set the contents of this {@link AbstractFileResource} to the given character array.
    */
   @Override
   public T setContents(final char[] data)
   {
      return setContents(new ByteArrayInputStream(new String(data).getBytes()));
   }

   @Override
   @SuppressWarnings("unchecked")
   /**
    * Set the contents of this {@link FileResource} to the contents of the given {@link InputStream}.
    */
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

         resourceFactory.fireEvent(new ResourceModified(this));
      }
      catch (IOException e)
      {
         throw new ResourceException(e);
      }
      return (T) this;
   }

   /**
    * Create the file in the underlying resource system. Necessary directory paths will be created automatically.
    */
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
            resourceFactory.fireEvent(new ResourceCreated(this));
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
   /**
    * Create a temporary {@link FileResource}
    */
   public T createTempResource()
   {
      try
      {
         T result = (T) createFrom(File.createTempFile("forgetemp", ""));
         resourceFactory.fireEvent(new TempResourceCreated(result));
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
      if (type.isAssignableFrom(result.getClass()))
      {
         return (R) result;
      }
      else
      {
         return null;
      }
   }

   /**
    * Rename this resource to the given path.
    */
   @Override
   public boolean renameTo(final String pathspec)
   {
      return renameTo(new File(pathspec));
   }

   /**
    * Rename this resource to the given {@link AbstractFileResource}
    */
   @Override
   public boolean renameTo(final FileResource<?> target)
   {
      return renameTo(target.getUnderlyingResourceObject());
   }

   /**
    * Rename this resource to the given {@link File} name.
    */
   private boolean renameTo(final File target)
   {
      File original = file.getAbsoluteFile();
      if (file.renameTo(target))
      {
         resourceFactory.fireEvent(new ResourceRenamed(this, original.getAbsolutePath(), file.getAbsolutePath()));
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

}
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
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.jboss.forge.addon.resource.monitor.ResourceMonitor;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
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
   public void markUpToDate()
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
            mkdirs();
            delete();
            if (!createNewFile())
            {
               throw new IOException("Failed to create file: " + file);
            }
         }

         OutputStream out = getFileOperations().createOutputStream(file);
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
         if (mkdirs())
         {
            delete();
         }
         if (getFileOperations().createNewFile(file))
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
      return resourceFactory.monitor(this);
   }

   @Override
   public ResourceMonitor monitor(ResourceFilter filter)
   {
      return resourceFactory.monitor(this, filter);
   }

   protected FileResourceOperations getFileOperations()
   {
      ResourceTransaction transaction;
      try
      {
         transaction = resourceFactory.getTransaction();
      }
      catch (UnsupportedOperationException uoe)
      {
         return DefaultFileOperations.INSTANCE;
      }
      if (transaction.isStarted())
      {
         // TODO: how will other resource types participate in a transaction? XA?
         return (FileResourceOperations) transaction;
      }
      else
      {
         return DefaultFileOperations.INSTANCE;
      }
   }

   private static enum DefaultFileOperations implements FileResourceOperations
   {
      INSTANCE;

      @Override
      public boolean fileExists(File f)
      {
         return f.exists();
      }

      @Override
      public boolean fileExistsAndIsDirectory(File f)
      {
         return f.isDirectory();
      }

      @Override
      public File[] listFiles(File f)
      {
         return f.listFiles();
      }

      @Override
      public long getFileLength(File f)
      {
         return f.length();
      }

      @Override
      public boolean deleteFile(File file)
      {
         return file.delete();
      }

      @Override
      public void deleteFileOnExit(File file)
      {
         file.deleteOnExit();
      }

      @Override
      public boolean createNewFile(File file) throws IOException
      {
         return file.createNewFile();
      }

      @Override
      public boolean mkdir(File file)
      {
         return file.mkdir();
      }

      @Override
      public boolean mkdirs(File file)
      {
         return file.mkdirs();
      }

      @Override
      public OutputStream createOutputStream(File file) throws IOException
      {
         return new FileOutputStream(file);
      }

      @Override
      public InputStream createInputStream(File file) throws IOException
      {
         return new BufferedInputStream(new FileInputStream(file));
      }

      @Override
      public boolean renameFile(File srcFile, File destFile)
      {
         if (srcFile == null)
         {
            throw new NullPointerException("Source must not be null");
         }
         if (destFile == null)
         {
            throw new NullPointerException("Destination must not be null");
         }
         return srcFile.renameTo(destFile);
      }

      /**
       * Copies a file to a new location.
       * <p>
       * This method copies the contents of the specified source file to the specified destination file. The directory
       * holding the destination file is created if it does not exist. If the destination file exists, then this method
       * will overwrite it.
       * <p>
       * <strong>Note:</strong> Setting <code>preserveFileDate</code> to <code>true</code> tries to preserve the file's
       * last modified date/times using {@link File#setLastModified(long)}, however it is not guaranteed that the
       * operation will succeed. If the modification operation fails, no indication is provided.
       * 
       * @param srcFile an existing file to copy, must not be <code>null</code>
       * @param destFile the new file, must not be <code>null</code>
       * @param preserveFileDate true if the file date of the copy should be the same as the original
       * 
       * @throws NullPointerException if source or destination is <code>null</code>
       * @throws IOException if source or destination is invalid
       * @throws IOException if an IO error occurs during copying
       * @see #copyFileToDirectory(File, File, boolean)
       */
      @Override
      public void copyFile(File srcFile, File destFile) throws IOException
      {
         if (srcFile == null)
         {
            throw new NullPointerException("Source must not be null");
         }
         if (destFile == null)
         {
            throw new NullPointerException("Destination must not be null");
         }
         if (srcFile.exists() == false)
         {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
         }
         if (srcFile.isDirectory())
         {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
         }
         if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath()))
         {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
         }
         if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false)
         {
            if (destFile.getParentFile().mkdirs() == false)
            {
               throw new IOException("Destination '" + destFile + "' directory cannot be created");
            }
         }
         if (destFile.exists() && destFile.canWrite() == false)
         {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
         }
         doCopyFile(srcFile, destFile);
      }

      /**
       * Internal copy file method.
       * 
       * @param srcFile the validated source file, must not be <code>null</code>
       * @param destFile the validated destination file, must not be <code>null</code>
       * @throws IOException if an error occurs
       */
      private void doCopyFile(File srcFile, File destFile) throws IOException
      {
         if (destFile.exists() && destFile.isDirectory())
         {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
         }

         FileInputStream fis = null;
         FileOutputStream fos = null;
         FileChannel input = null;
         FileChannel output = null;
         try
         {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            long FIFTY_MB = (1024L * 1024L) * 50L;
            while (pos < size)
            {
               count = (size - pos) > FIFTY_MB ? FIFTY_MB : (size - pos);
               pos += output.transferFrom(input, pos, count);
            }
         }
         finally
         {
            Streams.closeQuietly(output);
            Streams.closeQuietly(fos);
            Streams.closeQuietly(input);
            Streams.closeQuietly(fis);
         }

         if (srcFile.length() != destFile.length())
         {
            throw new IOException("Failed to copy full contents from '" +
                     srcFile + "' to '" + destFile + "'");
         }
      }
   }
}
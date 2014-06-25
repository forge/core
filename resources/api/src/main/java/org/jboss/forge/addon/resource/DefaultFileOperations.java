/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import org.jboss.forge.furnace.util.Streams;

/**
 * Default implementation for {@link File} based {@link ResourceOperations} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public enum DefaultFileOperations implements ResourceOperations<File>
{
   INSTANCE;

   @Override
   public boolean exists(File f)
   {
      return f.exists();
   }

   @Override
   public boolean existsAndIsDirectory(File f)
   {
      return f.isDirectory();
   }

   @Override
   public File[] listChildren(File f)
   {
      return f.listFiles();
   }

   @Override
   public long getLength(File f)
   {
      return f.length();
   }

   @Override
   public boolean delete(File file)
   {
      return file.delete();
   }

   @Override
   public void deleteOnExit(File file)
   {
      file.deleteOnExit();
   }

   @Override
   public boolean create(File file) throws ResourceException
   {
      try
      {
         return file.createNewFile();
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
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
   public OutputStream createOutputStream(File file) throws ResourceException
   {
      try
      {
         return new FileOutputStream(file);
      }
      catch (FileNotFoundException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   public InputStream createInputStream(File file) throws ResourceException
   {
      try
      {
         return new BufferedInputStream(new FileInputStream(file));
      }
      catch (FileNotFoundException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
   }

   @Override
   public boolean rename(File srcFile, File destFile)
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

   @Override
   public void copy(File srcFile, File destFile) throws ResourceException
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
         throw new ResourceException("Source '" + srcFile + "' does not exist");
      }
      if (srcFile.isDirectory())
      {
         throw new ResourceException("Source '" + srcFile + "' exists but is a directory");
      }
      try
      {
         if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath()))
         {
            throw new ResourceException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
         }
         if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false)
         {
            if (destFile.getParentFile().mkdirs() == false)
            {
               throw new ResourceException("Destination '" + destFile + "' directory cannot be created");
            }
         }
         if (destFile.exists() && destFile.canWrite() == false)
         {
            throw new ResourceException("Destination '" + destFile + "' exists but is read-only");
         }
         doCopyFile(srcFile, destFile);
      }
      catch (IOException e)
      {
         throw new ResourceException(e.getMessage(), e);
      }
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

   @Override
   public long getLastModifiedTime(File file)
   {
      return file.lastModified();
   }
}
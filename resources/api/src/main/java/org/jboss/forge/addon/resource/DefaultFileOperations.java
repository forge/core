/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Default implementation for {@link FileOperations} interface
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public enum DefaultFileOperations implements FileOperations
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
   public boolean deleteFile(File file, boolean recursive)
   {
      if (recursive)
      {
         if (file == null)
         {
            return false;
         }

         try
         {
            Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>()
            {
               @Override
               public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
               {
                  Files.delete(file);
                  return FileVisitResult.CONTINUE;
               }

               @Override
               public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
               {
                  Files.delete(dir);
                  return FileVisitResult.CONTINUE;
               }
            });
            return true;
         }
         catch (IOException e)
         {
            return false;
         }
      }
      return this.deleteFile(file);
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
   public OutputStream createOutputStream(File file, boolean append) throws IOException
   {
      return new FileOutputStream(file, append);
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

   @Override
   public File move(File source, File target) throws IOException
   {
      if (target.isDirectory())
      {
         Path path = Files.move(source.toPath(), Paths.get(target.getAbsolutePath(), source.getName()),
                  ATOMIC_MOVE);
         return path.toFile();
      }
      Path path = Files.move(source.toPath(), target.toPath(), ATOMIC_MOVE);
      return path.toFile();
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
      Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      if (srcFile.length() != destFile.length())
      {
         throw new IOException("Failed to copy full contents from '" +
                  srcFile + "' to '" + destFile + "'");
      }
   }
}
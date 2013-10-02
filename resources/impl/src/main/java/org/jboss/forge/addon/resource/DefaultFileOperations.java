/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public enum DefaultFileOperations implements FileResourceOperations
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
   public void moveFile(File src, File dest) throws IOException
   {
      FileUtils.moveFile(src, dest);
   }

   @Override
   public void copyFile(File src, File dest) throws IOException
   {
      FileUtils.copyFile(src, dest);
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
      return FileUtils.openOutputStream(file);
   }

   @Override
   public InputStream createInputStream(File file) throws IOException
   {
      return new BufferedInputStream(FileUtils.openInputStream(file));
   }
}

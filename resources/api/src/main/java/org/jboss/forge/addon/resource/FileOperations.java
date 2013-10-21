/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * File Operations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface FileOperations
{
   public boolean fileExists(File file);

   public boolean fileExistsAndIsDirectory(File file);

   public File[] listFiles(File file);

   public long getFileLength(File file);

   public boolean renameFile(File src, File dest);

   public void copyFile(File src, File dest) throws IOException;

   public boolean deleteFile(File file);

   public void deleteFileOnExit(File file);

   public boolean createNewFile(File file) throws IOException;

   public boolean mkdir(File file);

   public boolean mkdirs(File file);

   public OutputStream createOutputStream(File file) throws IOException;

   public InputStream createInputStream(File file) throws IOException;
}

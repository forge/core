/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
   boolean fileExists(File file);

   boolean fileExistsAndIsDirectory(File file);

   File[] listFiles(File file);

   long getFileLength(File file);

   boolean renameFile(File src, File dest);

   void copyFile(File src, File dest) throws IOException;

   boolean deleteFile(File file);

   boolean deleteFile(File file, boolean recursive);

   void deleteFileOnExit(File file);

   boolean createNewFile(File file) throws IOException;

   boolean mkdir(File file);

   boolean mkdirs(File file);

   OutputStream createOutputStream(File file) throws IOException;

   OutputStream createOutputStream(File file, boolean append) throws IOException;

   InputStream createInputStream(File file) throws IOException;

   File move(File source, File target) throws IOException;

}

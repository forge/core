/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class Files
{
   public static final String HOME_ALIAS = "~";
   public static final String SLASH = File.separator;

   /**
    * Replace instances of internal tokens with actual file equivalents.
    */
   public static String canonicalize(String target)
   {
      if (target.startsWith(Files.HOME_ALIAS))
      {
         String homePath = OSUtils.getUserHomePath();
         target = homePath + target.substring(1, target.length());
      }

      return target;
   }

   /**
    * Returns the current working directory
    * @return
    */
   public static File getWorkingDirectory()
   {
      return new File("").getAbsoluteFile();
   }

   /**
    * Unzips a specific file into a target directory
    */
   public static void unzip(File zipFile, File targetDirectory) throws IOException
   {
      OutputStream dest = null;
      ZipInputStream zis = new
               ZipInputStream(new BufferedInputStream(new
                        FileInputStream(zipFile)));
      ZipEntry entry;
      try
      {
         while ((entry = zis.getNextEntry()) != null)
         {
            File file = new File(targetDirectory, entry.getName());

            if (entry.isDirectory())
            {
               file.mkdirs();
               continue;
            }
            try
            {
               dest = new BufferedOutputStream(new FileOutputStream(file));
               Streams.write(zis, dest);
            }
            finally
            {
               dest.flush();
               Streams.closeQuietly(dest);
            }
         }
      }
      finally
      {
         Streams.closeQuietly(zis);
      }
   }

}

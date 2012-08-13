/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.util;

import java.io.File;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

   public static File getWorkingDirectory()
   {
      return new File("").getAbsoluteFile();
   }

}

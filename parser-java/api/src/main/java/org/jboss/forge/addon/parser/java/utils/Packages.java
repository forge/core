/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.utils;

import java.io.File;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class Packages
{

   /**
    * Convert a package name to file directory syntax.
    * <p>
    * Example: "com.example.package" becomes "com/example/package", using the directory separators correct for the
    * underlying operation system.
    */
   public static String toFileSyntax(String pkg)
   {
      return pkg.replace(".", File.separator);
   }

   public static String fromFileSyntax(String pkg)
   {
      return pkg.replace(File.separator, ".");
   }

   /**
    * Converts a given package name to a valid/parseable package name (without reserved words)
    */
   public static String toValidPackageName(String pkg)
   {
      if (pkg == null)
      {
         throw new IllegalArgumentException("Package should not be null");
      }
      // Sanitizing package name
      StringBuilder sb = new StringBuilder(pkg.length());
      for (int i = 0; i < pkg.length(); i++)
      {
         char c = pkg.charAt(i);
         // remove dots from the beginning and the end of the package
         if (c == '.' && (i == 0 || i == pkg.length() - 1))
         {
            continue;
         }
         if (c == '.' || Character.isJavaIdentifierPart(c))
         {
            sb.append(c);
         }
      }
      String packageName = sb.toString();
      StringBuilder result = new StringBuilder();
      String[] tokens = packageName.split("[.]");
      for (String token : tokens)
      {
         if (JLSValidator.isReservedWord(token))
         {
            token += "_";
         }
         result.append(token).append(".");
      }
      return result.substring(0, result.length() - 1);
   }
}

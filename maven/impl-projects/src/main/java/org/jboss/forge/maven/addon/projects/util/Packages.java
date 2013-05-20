/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.addon.projects.util;

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
      return pkg.replace(".", "/");
   }

   public static String fromFileSyntax(String pkg)
   {
      return pkg.replace("/", ".");
   }

   public static String toValidPackageName(String pkg)
   {
      if (pkg == null)
      {
         throw new IllegalArgumentException("Package should not be null");
      }
      StringBuilder sb = new StringBuilder(pkg.length());
      for (int i = 0; i < pkg.length(); i++)
      {
         char c = pkg.charAt(i);
         if (c == '.' || Character.isJavaIdentifierPart(c))
         {
            sb.append(c);
         }
      }
      return sb.toString();
   }
}

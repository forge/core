/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.util;

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
      boolean hasIdentifierStarted = false; 
      for (int i = 0; i < pkg.length(); i++)
      {
         int c = pkg.codePointAt(i);
         if(!hasIdentifierStarted)
         {
            if(Character.isJavaIdentifierStart(c))
            {
               sb.appendCodePoint(c);
               hasIdentifierStarted = true;
            }
         }
         else
         {
            if (Character.isJavaIdentifierPart(c))
            {
               sb.appendCodePoint(c);
            }
            else if(c == '.')
            {
               sb.appendCodePoint(c);
               hasIdentifierStarted = false;
            }
         }
      }
      return sb.toString();
   }
}

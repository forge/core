/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.facets;

import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * Allows manipulation of the current configured Java compiler version.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface JavaCompilerFacet extends ProjectFacet
{
   enum CompilerVersion
   {
      JAVA_1_3("1.3"),
      JAVA_1_4("1.4"),
      JAVA_1_5("1.5"),
      JAVA_1_6("1.6"),
      JAVA_1_7("1.7"),
      JAVA_1_8("1.8"),
      JAVA_9("9"),
      JAVA_10("10");

      String text;

      CompilerVersion(String text)
      {
         this.text = text;
      }

      @Override
      public String toString()
      {
         return text;
      }

      public static CompilerVersion getValue(String value)
      {
         for (CompilerVersion version : values())
         {
            if (version.toString().equals(value))
            {
               return version;
            }
         }
         throw new IllegalArgumentException(value + " is not a supported Java compiler version.");
      }
   }

   CompilerVersion DEFAULT_COMPILER_VERSION = CompilerVersion.JAVA_1_8;

   void setSourceCompilerVersion(CompilerVersion version);

   void setTargetCompilerVersion(CompilerVersion version);

   CompilerVersion getSourceCompilerVersion();

   CompilerVersion getTargetCompilerVersion();
}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell;

import org.jboss.forge.shell.util.Patterns;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum PromptType
{
   ANY(new String[] { ".*" }),
   DEPENDENCY_ID(new String[] { "[^:]+:[^:]+:?([^:]+:?){0,3}" }),
   JAVA_PACKAGE(
            new String[] { "(?i)(~\\.)?([a-z0-9_]+\\.?)+[a-z0-9_]",
                     "^(?!.*\\b(" + Patterns.JAVA_KEYWORDS + ")\\b.*).*$" }),
   JAVA_VARIABLE_NAME(new String[] { "^(?!(" + Patterns.JAVA_KEYWORDS + ")$)[A-Za-z0-9$_]+$" }),
   JAVA_CLASS(new String[] { "(?i)(~\\.)?([a-z0-9_]+\\.?)+[a-z0-9_]",
            "^(?!.*\\b(" + Patterns.JAVA_KEYWORDS + ")\\b.*).*$" }),
   FILE_PATH(new String[] { ".*" });

   private final String[] patterns;

   private PromptType(final String[] patterns)
   {
      this.patterns = patterns;
   }

   public boolean matches(final String value)
   {
      if (value == null)
      {
         return false;
      }

      for (int i = 0; i < patterns.length; i++)
      {
         if (!value.matches(patterns[i]))
         {
            return false;
         }
      }
      return true;
   }

}
/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

/**
 * Represents the various dependency scopes.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public enum ScopeType
{
   COMPILE("compile"),
   PROVIDED("provided"),
   RUNTIME("runtime"),
   TEST("test"),
   SYSTEM("system"),
   IMPORT("import"),
   OTHER("");

   private String scope;

   private ScopeType(final String scope)
   {
      this.scope = scope;
   }

   public String getScope()
   {
      return scope;
   }

   public static ScopeType from(final String type)
   {
      ScopeType result = null;

      if ((type != null) && !type.trim().isEmpty())
      {
         result = OTHER;
         for (ScopeType scopeType : ScopeType.values())
         {
            if (scopeType.getScope().equalsIgnoreCase(type.trim()))
            {
               result = scopeType;
            }
         }
      }
      return result;
   }
}

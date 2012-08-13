/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.util;

import org.mvel2.util.ParseTools;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class Types
{
   public static String getTypeDescriptor(Class<?> type)
   {
      if (Number.class.isAssignableFrom(ParseTools.boxPrimitive(type)))
      {
         return "numeric";
      }
      else
      {
         return type.getCanonicalName();
      }
   }
}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.classloader.mock.collisions;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ClassCreatesInstanceFromClassLoader
{
   @SuppressWarnings("unchecked")
   public <T> T create(ClassLoader loader, Class<T> type)
   {
      try
      {
         return (T) loader.loadClass(type.getName()).newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error creating type by name and default constructor: " + type.getName(), e);
      }
   }
}

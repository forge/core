/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee;

import java.util.concurrent.Callable;

import org.jboss.forge.furnace.util.ClassLoaders;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;

/**
 * Descriptors that sets the context {@link ClassLoader} to the target class
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public final class Descriptors
{

   public static <T extends Descriptor> T create(final Class<T> type) throws IllegalArgumentException
   {
      try
      {
         return ClassLoaders.executeIn(type.getClassLoader(), new Callable<T>()
         {
            @Override
            public T call() throws Exception
            {
               return org.jboss.shrinkwrap.descriptor.api.Descriptors.create(type);
            }
         });
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static <T extends Descriptor> T create(final Class<T> type, final String descriptorName)
            throws IllegalArgumentException
   {
      try
      {
         return ClassLoaders.executeIn(type.getClassLoader(), new Callable<T>()
         {
            @Override
            public T call() throws Exception
            {
               return org.jboss.shrinkwrap.descriptor.api.Descriptors.create(type, descriptorName);
            }
         });
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static <T extends Descriptor> DescriptorImporter<T> importAs(final Class<T> type)
            throws IllegalArgumentException
   {
      try
      {
         return ClassLoaders.executeIn(type.getClassLoader(), new Callable<DescriptorImporter<T>>()
         {
            @Override
            public DescriptorImporter<T> call() throws Exception
            {
               return org.jboss.shrinkwrap.descriptor.api.Descriptors.importAs(type);
            }
         });
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static <T extends Descriptor> DescriptorImporter<T> importAs(final Class<T> type, final String descriptorName)
            throws IllegalArgumentException
   {
      try
      {
         return ClassLoaders.executeIn(type.getClassLoader(), new Callable<DescriptorImporter<T>>()
         {
            @Override
            public DescriptorImporter<T> call() throws Exception
            {
               return org.jboss.shrinkwrap.descriptor.api.Descriptors.importAs(type, descriptorName);
            }
         });
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}

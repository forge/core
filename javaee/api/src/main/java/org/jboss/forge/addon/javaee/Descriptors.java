/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee;

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
      Thread currentThread = Thread.currentThread();
      ClassLoader tccl = currentThread.getContextClassLoader();
      try
      {
         currentThread.setContextClassLoader(type.getClassLoader());
         return org.jboss.shrinkwrap.descriptor.api.Descriptors.create(type);
      }
      finally
      {
         currentThread.setContextClassLoader(tccl);
      }
   }

   public static <T extends Descriptor> T create(final Class<T> type, String descriptorName)
            throws IllegalArgumentException
   {
      Thread currentThread = Thread.currentThread();
      ClassLoader tccl = currentThread.getContextClassLoader();
      try
      {
         currentThread.setContextClassLoader(type.getClassLoader());
         return org.jboss.shrinkwrap.descriptor.api.Descriptors.create(type, descriptorName);
      }
      finally
      {
         currentThread.setContextClassLoader(tccl);
      }
   }

   public static <T extends Descriptor> DescriptorImporter<T> importAs(final Class<T> type)
            throws IllegalArgumentException
   {
      Thread currentThread = Thread.currentThread();
      ClassLoader tccl = currentThread.getContextClassLoader();
      try
      {
         currentThread.setContextClassLoader(type.getClassLoader());
         return org.jboss.shrinkwrap.descriptor.api.Descriptors.importAs(type);
      }
      finally
      {
         currentThread.setContextClassLoader(tccl);
      }

   }

   public static <T extends Descriptor> DescriptorImporter<T> importAs(final Class<T> type, String descriptorName)
            throws IllegalArgumentException
   {
      Thread currentThread = Thread.currentThread();
      ClassLoader tccl = currentThread.getContextClassLoader();
      try
      {
         currentThread.setContextClassLoader(type.getClassLoader());
         return org.jboss.shrinkwrap.descriptor.api.Descriptors.importAs(type, descriptorName);
      }
      finally
      {
         currentThread.setContextClassLoader(tccl);
      }

   }
}

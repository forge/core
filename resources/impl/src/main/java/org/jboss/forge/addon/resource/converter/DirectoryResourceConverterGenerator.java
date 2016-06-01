/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.converter;

import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Generates {@link DirectoryResourceConverter}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class DirectoryResourceConverterGenerator implements ConverterGenerator
{
   private DirectoryResourceConverter directoryResourceConverter;

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return DirectoryResource.class.isAssignableFrom(target);
   }

   @Override
   public DirectoryResourceConverter generateConverter(Class<?> source, Class<?> target)
   {
      if (directoryResourceConverter == null)
      {
         ResourceFactory resourceFactory = SimpleContainer
                  .getServices(getClass().getClassLoader(), ResourceFactory.class).get();
         directoryResourceConverter = new DirectoryResourceConverter(resourceFactory);
      }
      return directoryResourceConverter;
   }

   @Override
   public Class<DirectoryResourceConverter> getConverterType()
   {
      return DirectoryResourceConverter.class;
   }
}

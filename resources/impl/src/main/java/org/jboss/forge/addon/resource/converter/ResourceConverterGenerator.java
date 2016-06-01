/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.converter;

import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

public class ResourceConverterGenerator implements ConverterGenerator
{

   private ResourceConverter converter;

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      /*
       * All resources except DirectoryResource and FileResource, because it will be handled by
       * DirectoryResourceConverter
       */
      return Resource.class.isAssignableFrom(target)
               && !DirectoryResource.class.isAssignableFrom(target)
               && !FileResource.class.isAssignableFrom(target);
   }

   @Override
   public ResourceConverter generateConverter(Class<?> source, Class<?> target)
   {
      if (converter == null)
      {
         ResourceFactory resourceFactory = SimpleContainer
                  .getServices(getClass().getClassLoader(), ResourceFactory.class).get();
         converter = new ResourceConverter(resourceFactory);
      }
      return converter;
   }

   @Override
   public Class<ResourceConverter> getConverterType()
   {
      return ResourceConverter.class;
   }
}

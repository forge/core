/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.converter;

import java.io.File;

import org.jboss.forge.addon.convert.AbstractConverter;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Strings;

/**
 * Converts a {@link File} object to a {@link Resource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */

public class DirectoryResourceConverter extends AbstractConverter<Object, DirectoryResource>
{
   private final ResourceFactory resourceFactory;

   public DirectoryResourceConverter()
   {
      super(Object.class, DirectoryResource.class);
      this.resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   public DirectoryResourceConverter(ResourceFactory resourceFactory)
   {
      super(Object.class, DirectoryResource.class);
      this.resourceFactory = resourceFactory;
   }

   @Override
   public DirectoryResource convert(Object source)
   {
      File file;
      if (source == null || Strings.isNullOrEmpty(source.toString()))
         return null;
      else if (source instanceof File)
         file = (File) source;
      else
         file = new File(source.toString());
      return resourceFactory.create(DirectoryResource.class, file);
   }
}
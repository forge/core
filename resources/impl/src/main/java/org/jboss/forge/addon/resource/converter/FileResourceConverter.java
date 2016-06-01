/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.converter;

import java.io.File;

import org.jboss.forge.addon.convert.AbstractConverter;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Strings;

/**
 * Converts a {@link File} object to a {@link Resource}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */

public class FileResourceConverter extends AbstractConverter<Object, FileResource>
{
   private final ResourceFactory resourceFactory;

   public FileResourceConverter()
   {
      super(Object.class, FileResource.class);
      this.resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   public FileResourceConverter(ResourceFactory resourceFactory)
   {
      super(Object.class, FileResource.class);
      this.resourceFactory = resourceFactory;
   }

   @SuppressWarnings("unchecked")
   @Override
   public FileResource<?> convert(Object source)
   {
      File file;
      if (source == null || Strings.isNullOrEmpty(source.toString()))
         return null;
      else if (source instanceof File)
         file = (File) source;
      else
         file = new File(source.toString());

      return resourceFactory.create(FileResource.class, file);
   }
}
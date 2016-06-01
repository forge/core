/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.converter;

import java.io.File;

import org.jboss.forge.addon.convert.AbstractConverter;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Converts a {@link File} object to a {@link Resource}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@SuppressWarnings("rawtypes")
public class ResourceConverter extends AbstractConverter<Object, Resource>
{
   private final ResourceFactory resourceFactory;

   public ResourceConverter()
   {
      super(Object.class, Resource.class);
      this.resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   public ResourceConverter(ResourceFactory resourceFactory)
   {
      super(Object.class, Resource.class);
      this.resourceFactory = resourceFactory;
   }

   @Override
   public Resource<?> convert(Object source)
   {
      return resourceFactory.create(source);
   }
}
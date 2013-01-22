/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource.converter;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;

/**
 * Converts a {@link File} object to a {@link Resource}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Exported
public class ResourceConverter implements Converter<Object, Resource<?>>
{
   private final ResourceFactory resourceFactory;

   @Inject
   public ResourceConverter(ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   @Override
   public Resource<?> convert(Object source)
   {
      return resourceFactory.create(source);
   }

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      // TODO: Shall the source be checked ?
      return Resource.class.isAssignableFrom(target);
   }
}
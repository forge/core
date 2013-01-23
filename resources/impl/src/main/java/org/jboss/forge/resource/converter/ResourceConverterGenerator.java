/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource.converter;

import javax.annotation.Resource;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.convert.ConverterGenerator;

@Exported
public class ResourceConverterGenerator implements ConverterGenerator
{

   @Inject
   private Instance<ResourceConverter> converter;

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return Resource.class.isAssignableFrom(target);
   }

   @Override
   public ResourceConverter generateConverter()
   {
      return converter.get();
   }

   @Override
   public Class<ResourceConverter> getConverterType()
   {
      return ResourceConverter.class;
   }
}

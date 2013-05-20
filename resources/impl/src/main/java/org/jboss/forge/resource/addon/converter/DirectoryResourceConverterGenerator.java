/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource.addon.converter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.container.services.Exported;

/**
 * Generates {@link DirectoryResourceConverter}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Exported
public class DirectoryResourceConverterGenerator implements ConverterGenerator
{

   @Inject
   private Instance<DirectoryResourceConverter> converter;

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return DirectoryResource.class.isAssignableFrom(target);
   }

   @Override
   public DirectoryResourceConverter generateConverter(Class<?> source, Class<?> target)
   {
      return converter.get();
   }

   @Override
   public Class<DirectoryResourceConverter> getConverterType()
   {
      return DirectoryResourceConverter.class;
   }
}

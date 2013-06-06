/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl.converter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterGenerator;
import org.jboss.forge.addon.projects.ProjectType;

public class ProjectTypeConverterGenerator implements ConverterGenerator
{
   @Inject
   private Instance<ProjectTypeConverter> instance;

   @Override
   public boolean handles(Class<?> source, Class<?> target)
   {
      return ProjectType.class.isAssignableFrom(target);
   }

   @Override
   public Converter<?, ?> generateConverter(Class<?> source, Class<?> target)
   {
      return instance.get();
   }

   @Override
   public Class<? extends Converter<?, ?>> getConverterType()
   {
      return ProjectTypeConverter.class;
   }

}

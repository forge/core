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
import org.jboss.forge.converter.Converter;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;

/**
 * Converts a {@link File} object to a {@link Resource}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Exported
public class FileResourceConverter implements Converter<File, Resource<File>>
{
   private final ResourceFactory resourceFactory;

   @Inject
   public FileResourceConverter(ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   @Override
   public Resource<File> convert(File source) throws Exception
   {
      return resourceFactory.create(source);
   }
}
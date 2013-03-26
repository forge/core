/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource.converter;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.convert.AbstractConverter;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;

/**
 * Converts a {@link File} object to a {@link Resource}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */

@Singleton
public class DirectoryResourceConverter extends AbstractConverter<File, DirectoryResource>
{
   private final ResourceFactory resourceFactory;

   @Inject
   public DirectoryResourceConverter(ResourceFactory resourceFactory)
   {
      super(File.class, DirectoryResource.class);
      this.resourceFactory = resourceFactory;
   }

   @Override
   public DirectoryResource convert(File source)
   {
      return resourceFactory.create(DirectoryResource.class, source);
   }
}
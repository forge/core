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
import org.jboss.forge.resource.FileResource;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;

/**
 * Converts a {@link File} object to a {@link Resource}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */

@Singleton
@SuppressWarnings("rawtypes")
public class FileResourceConverter extends AbstractConverter<Object, FileResource>
{
   private final ResourceFactory resourceFactory;

   @Inject
   public FileResourceConverter(ResourceFactory resourceFactory)
   {
      super(Object.class, FileResource.class);
      this.resourceFactory = resourceFactory;
   }

   @Override
   @SuppressWarnings("unchecked")
   public FileResource convert(Object source)
   {
      File file = null;
      if (source instanceof File)
         file = (File) source;
      else
         file = new File(source.toString());
      return resourceFactory.create(FileResource.class, file);
   }
}
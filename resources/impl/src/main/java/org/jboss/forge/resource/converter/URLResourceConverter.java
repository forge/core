/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource.converter;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import org.jboss.forge.convert.Converter;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;

/**
 * Converts a {@link File} object to a {@link Resource}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class URLResourceConverter implements Converter<URL, Resource<URL>>
{
   private final ResourceFactory resourceFactory;

   @Inject
   public URLResourceConverter(ResourceFactory resourceFactory)
   {
      this.resourceFactory = resourceFactory;
   }

   @Override
   public Resource<URL> convert(URL source) throws Exception
   {
      return resourceFactory.create(source);
   }
}
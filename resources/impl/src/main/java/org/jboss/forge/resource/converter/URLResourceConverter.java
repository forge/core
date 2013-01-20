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

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.convert.BaseConverter;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.resource.URLResource;

/**
 * Converts a {@link File} object to a {@link Resource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
@Exported
public class URLResourceConverter extends BaseConverter<URL, URLResource>
{
   private final ResourceFactory resourceFactory;

   @Inject
   public URLResourceConverter(ResourceFactory resourceFactory)
   {
      super(URL.class, URLResource.class);
      this.resourceFactory = resourceFactory;
   }

   @Override
   public URLResource convert(URL source)
   {
      return (URLResource) resourceFactory.create(source);
   }
}
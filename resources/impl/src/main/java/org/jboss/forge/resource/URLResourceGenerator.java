/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.resource;

import java.net.URL;

import org.jboss.forge.container.services.Exported;

/**
 * Generates {@link URLResource} objects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Exported
public class URLResourceGenerator implements ResourceGenerator<URLResource, URL>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      return (resource instanceof URL);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<URL>> T getResource(ResourceFactory factory, Class<URLResource> type, URL resource)
   {
      return (T) new URLResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<URL>> Class<?> getResourceType(Class<URLResource> type, URL resource)
   {
      return type;
   }

}

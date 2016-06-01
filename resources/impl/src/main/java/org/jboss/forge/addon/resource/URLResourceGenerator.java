/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.forge.furnace.util.Strings;

/**
 * Generates {@link URLResource} objects
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class URLResourceGenerator implements ResourceGenerator<URLResource, Object>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      boolean result;
      if (resource == null)
      {
         result = false;
      }
      else if (resource instanceof URL)
      {
         result = true;
      }
      else
      {
         result = Strings.isURL(resource.toString());
      }
      return result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<Object>> T getResource(ResourceFactory factory, Class<URLResource> type, Object resource)
   {
      URL url = null;
      if (resource instanceof URL)
      {
         url = (URL) resource;
      }
      else if (Strings.isURL(resource.toString()))
      {
         try
         {
            url = new URL(resource.toString());
         }
         catch (MalformedURLException e)
         {
            // shouldn't happen
            throw new IllegalArgumentException("Invalid URL found", e);
         }
      }

      Resource<?> createdResource = new URLResourceImpl(factory, url);
      return (T) createdResource;
   }

   @Override
   public <T extends Resource<Object>> Class<?> getResourceType(ResourceFactory factory, Class<URLResource> type,
            Object resource)
   {
      return type;
   }

}

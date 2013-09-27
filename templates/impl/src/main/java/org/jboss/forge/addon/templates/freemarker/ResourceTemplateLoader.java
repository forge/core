/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.templates.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;

import freemarker.cache.TemplateLoader;

/**
 * Loader for Resource objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ResourceTemplateLoader implements TemplateLoader
{
   private Map<String, ResourceId> resourceMap = new ConcurrentHashMap<String, ResourceTemplateLoader.ResourceId>();

   String register(Resource<?> resource)
   {
      String id = UUID.randomUUID().toString();
      resourceMap.put(id, new ResourceId(id, resource));
      return id;
   }

   void dispose(String id)
   {
      resourceMap.remove(id);
   }

   @Override
   public Object findTemplateSource(String name) throws IOException
   {
      return resourceMap.get(name);
   }

   @Override
   public long getLastModified(Object templateSource)
   {
      ResourceId resourceId = (ResourceId) templateSource;
      Resource<?> resource = resourceId.resource;
      if (resource instanceof FileResource)
      {
         return ((FileResource<?>) resource).getUnderlyingResourceObject().lastModified();
      }
      return 0L;
   }

   @Override
   public Reader getReader(Object templateSource, String encoding) throws IOException
   {
      ResourceId resourceId = (ResourceId) templateSource;
      return new InputStreamReader(resourceId.resource.getResourceInputStream(), encoding);
   }

   @Override
   public void closeTemplateSource(Object templateSource) throws IOException
   {
      ResourceId resourceId = (ResourceId) templateSource;
      dispose(resourceId.id);
   }

   private static class ResourceId
   {
      final String id;
      final Resource<?> resource;

      ResourceId(String id, Resource<?> resource)
      {
         super();
         this.id = id;
         this.resource = resource;
      }
   }

}

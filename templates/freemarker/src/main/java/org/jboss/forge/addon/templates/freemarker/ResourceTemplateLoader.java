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
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;

import freemarker.cache.StatefulTemplateLoader;

/**
 * Loader for Resource objects
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class ResourceTemplateLoader implements StatefulTemplateLoader
{
   private Map<String, ResourceId> resourceMap = new ConcurrentHashMap<String, ResourceId>();

   /**
    * Needed for includes
    */
   @Inject
   private ResourceFactory resourceFactory;

   String register(Resource<?> resource)
   {
      ResourceId resourceId = generateResourceId(resource);
      resourceMap.put(resourceId.id, resourceId);
      return resourceId.id;
   }

   void dispose(String id)
   {
      resourceMap.remove(id);
   }

   @Override
   public Object findTemplateSource(String name) throws IOException
   {
      ResourceId resource = resourceMap.get(name);
      if (resource == null)
      {
         Resource<?> includedResource = resourceFactory.create(name);
         if (includedResource != null && includedResource.exists())
         {
            resource = generateResourceId(includedResource);
         }
      }
      return resource;
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

   @Override
   public void resetState()
   {
      resourceMap.clear();
   }

   private ResourceId generateResourceId(Resource<?> resource)
   {
      String id = resource.getName();
      return new ResourceId(id, resource);
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

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.templates.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

import freemarker.cache.StatefulTemplateLoader;

/**
 * Loader for Resource objects
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResourceTemplateLoader implements StatefulTemplateLoader
{
   private final Map<String, ResourceId> resourceMap = new ConcurrentHashMap<>();

   private ResourceFactory resourceFactory;

   String register(Resource<?> resource)
   {
      ResourceId resourceId = generateResourceId(resource);
      resourceMap.put(resourceId.getId(), resourceId);
      return resourceId.getId();
   }

   void dispose(String id)
   {
      resourceMap.remove(id);
   }

   @Override
   public Object findTemplateSource(String name) throws IOException
   {
      ResourceId id = resourceMap.get(name);
      if (id == null)
      {
         for (Entry<String, ResourceId> entry : resourceMap.entrySet())
         {
            if (name.startsWith(entry.getKey()))
            {
               id = entry.getValue();
               break;
            }
         }
      }

      if (id == null)
      {
         Resource<?> includedResource = getResourceFactory().create(name);
         if (includedResource != null && includedResource.exists())
         {
            id = generateResourceId(includedResource);
         }
      }
      return id;
   }

   @Override
   public long getLastModified(Object templateSource)
   {
      ResourceId resourceId = (ResourceId) templateSource;
      Resource<?> resource = resourceId.getResource();
      if (resource instanceof FileResource)
      {
         return ((FileResource<?>) resource).getLastModified();
      }
      return 0L;
   }

   @Override
   public Reader getReader(Object templateSource, String encoding) throws IOException
   {
      ResourceId resourceId = (ResourceId) templateSource;
      return new InputStreamReader(resourceId.getResource().getResourceInputStream(), encoding);
   }

   @Override
   public void closeTemplateSource(Object templateSource) throws IOException
   {
      ResourceId resourceId = (ResourceId) templateSource;
      dispose(resourceId.getId());
   }

   @Override
   public void resetState()
   {
      resourceMap.clear();
   }

   private ResourceId generateResourceId(Resource<?> resource)
   {
      return new ResourceId(resource);
   }

   private ResourceFactory getResourceFactory()
   {
      if (resourceFactory == null)
      {
         AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
         resourceFactory = addonRegistry.getServices(ResourceFactory.class).get();
      }
      return resourceFactory;
   }

}

package org.jboss.forge.addon.resource.zip;

import java.io.File;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;
import org.jboss.forge.addon.resource.zip.ZipResource;

public class ZipResourceGenerator implements ResourceGenerator<ZipResource, File>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      return (resource instanceof File) && ZipResource.class.isAssignableFrom(type);
   }

   @Override
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<ZipResource> type, File resource)
   {
      return (T) new ZipResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<ZipResource> type,
            File resource)
   {
      return ZipResource.class;
   }
}

package org.jboss.forge.addon.parser.xml.resources;

import java.io.File;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

public class XMLResourceGenerator implements ResourceGenerator<XMLResource, File>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof File && ((File) resource).getName().endsWith(".xml"))
      {
         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<XMLResource> type, File resource)
   {
      return (T) new XMLResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(Class<XMLResource> type, File resource)
   {
      return XMLResource.class;
   }
}

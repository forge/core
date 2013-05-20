package org.jboss.forge.addon.parser.java.resources;

import java.io.File;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;
import org.jboss.forge.container.services.Exported;

@Exported
public class JavaResourceGenerator implements ResourceGenerator<JavaResource, File>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof File && ((File) resource).getName().endsWith(".java"))
      {
         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<JavaResource> type, File resource)
   {
      return (T) new JavaResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(Class<JavaResource> type, File resource)
   {
      return JavaResource.class;
   }
}

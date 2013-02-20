package org.jboss.forge.parser.java.resources;

import java.io.File;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.resource.ResourceGenerator;

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
      return (T) new JavaResource(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(Class<JavaResource> type, File resource)
   {
      return JavaResource.class;
   }
}

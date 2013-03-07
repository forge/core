package org.jboss.forge.maven.resources;

import java.io.File;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.resource.ResourceGenerator;

@Exported
public class MavenResourceGenerator implements ResourceGenerator<MavenPomResource, File>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof File && ((File) resource).getName().equals("pom.xml"))
      {
         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<MavenPomResource> type, File resource)
   {
      return (T) new MavenPomResource(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(Class<MavenPomResource> type, File resource)
   {
      return MavenPomResource.class;
   }
}

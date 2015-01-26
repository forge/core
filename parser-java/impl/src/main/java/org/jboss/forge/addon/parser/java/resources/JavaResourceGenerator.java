package org.jboss.forge.addon.parser.java.resources;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

public class JavaResourceGenerator implements ResourceGenerator<JavaResource, File>
{

   @Inject
   private Configuration userConfig;

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
      String formatterProfileName = userConfig.getString(JavaResource.FORMATTER_PROFILE_PATH_KEY);
      String formatterProfilePath = userConfig.getString(JavaResource.FORMATTER_PROFILE_PATH_KEY);
      return (T) new JavaResourceImpl(factory, resource, formatterProfileName, formatterProfilePath);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<JavaResource> type,
            File resource)
   {
      return JavaResource.class;
   }
}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script.resource.impl;

import java.io.File;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;
import org.jboss.forge.addon.script.resource.ScriptFileResource;

/**
 * Generates {@link ScriptFileResource} objects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ScriptFileResourceGenerator implements ResourceGenerator<ScriptFileResource, File>
{

   private ScriptEngineManager manager;

   public ScriptFileResourceGenerator()
   {
      manager = new ScriptEngineManager(getClass().getClassLoader());
   }

   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      boolean result = false;
      if (resource instanceof File)
      {
         String extension = getFileExtension((File) resource);
         result = (extension != null && manager.getEngineByExtension(extension.toLowerCase()) != null);
      }
      return result;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<ScriptFileResource> type,
            File resource)
   {
      String fileExtension = getFileExtension(resource);
      ScriptEngine engine = manager.getEngineByExtension(fileExtension.toLowerCase());
      return (T) new ScriptFileResourceImpl(factory, resource, engine);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<ScriptFileResource> type,
            File resource)
   {
      return ScriptFileResource.class;
   }

   private String getFileExtension(File file)
   {
      String name = file.getName();
      int idx = name.lastIndexOf('.');
      if (idx > -1)
      {
         return name.substring(idx + 1);
      }
      return null;
   }

}

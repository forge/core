/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.yaml.resource.generator;

import java.io.File;

import org.jboss.forge.addon.parser.yaml.resource.YamlResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

/**
 * Default {@link ResourceGenerator} implementation for {@link YamlResource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class YamlResourceGenerator implements ResourceGenerator<YamlResource, File>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (!(resource instanceof File))
      {
         // Do not handle non-file resources (yet)
         return false;
      }
      File fileResource = (File) resource;
      String fileName = fileResource.getName().toLowerCase();
      if (!fileResource.isDirectory()
               && (type == YamlResource.class || (fileName.endsWith(".yaml") || fileName.endsWith(".yml"))))
      {
         return true;
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<YamlResource> type, File resource)
   {
      return (T) new YamlResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<YamlResource> type,
            File resource)
   {
      return YamlResource.class;
   }

}

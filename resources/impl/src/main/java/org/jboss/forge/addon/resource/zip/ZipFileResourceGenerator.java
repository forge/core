/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.zip;

import java.io.File;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

/**
 * Implementation of {@link ResourceGenerator} for {@link ZipFileResource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ZipFileResourceGenerator implements ResourceGenerator<ZipFileResource, File>
{
   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof File)
      {
         return (type == ZipFileResource.class || ((File) resource).getName().toLowerCase().endsWith(".zip"));
      }
      return false;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<ZipFileResource> type, File resource)
   {
      return (T) new ZipFileResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<ZipFileResource> type,
            File resource)
   {
      return ZipFileResource.class;
   }
}

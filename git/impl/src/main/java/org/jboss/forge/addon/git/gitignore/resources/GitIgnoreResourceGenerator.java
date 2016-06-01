/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.gitignore.resources;

import static org.jboss.forge.addon.git.constants.GitConstants.GITIGNORE;

import java.io.File;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.ResourceGenerator;

public class GitIgnoreResourceGenerator implements ResourceGenerator<GitIgnoreResourceImpl, File>
{

   @Override
   public boolean handles(Class<?> type, Object resource)
   {
      if (resource instanceof File && ((File) resource).getName().equals(GITIGNORE))
      {
         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends Resource<File>> T getResource(ResourceFactory factory, Class<GitIgnoreResourceImpl> type, File resource)
   {
      return (T) new GitIgnoreResourceImpl(factory, resource);
   }

   @Override
   public <T extends Resource<File>> Class<?> getResourceType(ResourceFactory factory, Class<GitIgnoreResourceImpl> type,
            File resource)
   {
      return GitIgnoreResourceImpl.class;
   }

}

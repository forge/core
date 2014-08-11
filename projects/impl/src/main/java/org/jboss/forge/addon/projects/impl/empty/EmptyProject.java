/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl.empty;

import org.jboss.forge.addon.projects.AbstractProject;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.util.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class EmptyProject extends AbstractProject
{
   private final Resource<?> root;

   public EmptyProject(Resource<?> root)
   {
      Assert.notNull(root, "Project root cannot be null");
      this.root = root;
   }

   @Override
   public boolean supports(ProjectFacet facet)
   {
      return true;
   }

   @Override
   public DirectoryResource getRootDirectory()
   {
      return root.reify(DirectoryResource.class);
   }

   @Override
   public Resource<?> getRoot()
   {
      return root;
   }

}

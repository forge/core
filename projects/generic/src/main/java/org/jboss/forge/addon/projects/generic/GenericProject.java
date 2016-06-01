/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.generic;

import org.jboss.forge.addon.projects.AbstractProject;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.Resource;

/**
 * A generic implementation of {@link Project}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class GenericProject extends AbstractProject
{
   private final Resource<?> root;

   public GenericProject(Resource<?> projectRoot)
   {
      this.root = projectRoot;
   }

   @Override
   public Resource<?> getRoot()
   {
      return root;
   }

   @Override
   public <F extends ProjectFacet> boolean supports(F facet)
   {
      return true;
   }

   @Override
   public String toString()
   {
      return root.toString();
   }

}

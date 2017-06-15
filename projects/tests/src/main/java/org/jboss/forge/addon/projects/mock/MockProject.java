/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.mock;

import org.jboss.forge.addon.projects.AbstractProject;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.Resource;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class MockProject extends AbstractProject
{

   private final Resource<?> root;

   public MockProject(Resource<?> root)
   {
      this.root = root;
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
}

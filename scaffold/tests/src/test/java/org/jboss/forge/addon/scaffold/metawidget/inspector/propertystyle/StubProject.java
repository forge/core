/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle;

import org.jboss.forge.addon.projects.AbstractProject;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.resource.Resource;

/**
 * A stubbed out implementation of a Project
 */
public class StubProject extends AbstractProject
{

   @Override
   public Resource<?> getRoot()
   {
      return null;
   }

   @Override
   public <F extends ProjectFacet> boolean supports(F facet)
   {
      return false;
   }

}

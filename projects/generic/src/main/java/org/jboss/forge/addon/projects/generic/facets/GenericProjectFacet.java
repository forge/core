/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.generic.facets;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.projects.generic.GenericProjectProvider;
import org.jboss.forge.addon.projects.generic.GenericProjectType;

/**
 * Generic projects should not supported by any other build system. This should be used only in
 * {@link GenericProjectProvider} and {@link GenericProjectType}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class GenericProjectFacet extends AbstractFacet<Project> implements ProvidedProjectFacet
{
   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return false;
   }
}

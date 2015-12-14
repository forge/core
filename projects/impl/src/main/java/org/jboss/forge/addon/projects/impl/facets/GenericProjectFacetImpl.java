/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.impl.facets;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.facets.generic.GenericProjectFacet;
import org.jboss.forge.addon.projects.types.GenericProjectType;

/**
 * This facet is needed so other {@link ProjectProvider} implementations (Maven, Gradle) won't support the
 * {@link GenericProjectType}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class GenericProjectFacetImpl extends AbstractFacet<Project> implements GenericProjectFacet
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

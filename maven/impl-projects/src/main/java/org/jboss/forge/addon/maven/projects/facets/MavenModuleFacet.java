/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.util.List;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ModuleFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;

/**
 * {@link ModuleFacet} implementation for Maven
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraints({
         @FacetConstraint(MavenFacet.class),
         @FacetConstraint(PackagingFacet.class)
})
public class MavenModuleFacet extends AbstractFacet<Project> implements ModuleFacet
{

   @Override
   public boolean install()
   {
      PackagingFacet facet = getFaceted().getFacet(PackagingFacet.class);
      facet.setPackagingType("pom");
      return isInstalled();
   }

   @Override
   public boolean isInstalled()
   {
      PackagingFacet facet = getFaceted().getFacet(PackagingFacet.class);
      return "pom".equals(facet.getPackagingType());
   }

   @Override
   public List<String> getModules()
   {
      MavenFacet facet = getFaceted().getFacet(MavenFacet.class);
      return facet.getModel().getModules();
   }
}

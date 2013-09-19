/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.types;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenDependencyFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenJavaCompilerFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenJavaSourceFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenMetadataFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenPackagingFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenResourceFacet;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/*
 * 
 */
public class MavenJarProjectType implements ProjectType
{
   @Override
   public String getType()
   {
      return "Maven - Java";
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return null;
   }

   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      List<Class<? extends ProjectFacet>> result = new ArrayList<Class<? extends ProjectFacet>>(6);
      result.add(MavenFacet.class);
      result.add(MavenMetadataFacet.class);
      result.add(MavenPackagingFacet.class);
      result.add(MavenDependencyFacet.class);
      result.add(MavenResourceFacet.class);
      result.add(MavenJavaCompilerFacet.class);
      result.add(MavenJavaSourceFacet.class);
      return result;
   }

   @Override
   public String toString()
   {
      return "jar";
   }
}

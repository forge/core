/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.BuildSystemFacet;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * A {@link ProjectType} that represents the simple JAR layout.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaProjectType implements ProjectType
{
   @Override
   public String getType()
   {
      return "Java Library";
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      return null;
   }

   @Override
   public Iterable<Class<? extends BuildSystemFacet>> getRequiredBuildSystemFacets()
   {
      List<Class<? extends BuildSystemFacet>> result = new ArrayList<Class<? extends BuildSystemFacet>>(6);
      result.add(MetadataFacet.class);
      result.add(PackagingFacet.class);
      result.add(DependencyFacet.class);
      result.add(ResourcesFacet.class);
      result.add(JavaCompilerFacet.class);
      result.add(JavaSourceFacet.class);
      return result;
   }

   @Override
   public Iterable<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      return Collections.emptySet();
   }

   @Override
   public String toString()
   {
      return "jar";
   }
}

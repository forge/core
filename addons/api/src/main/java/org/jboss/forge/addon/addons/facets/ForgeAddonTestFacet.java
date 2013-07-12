/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons.facets;

import java.io.FileNotFoundException;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.RequiresFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;

/**
 * Configures the project as an Addon Test project
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
@RequiresFacet({ ForgeContainerAddonFacet.class })
public class ForgeAddonTestFacet extends AbstractFacet<Project> implements ProjectFacet
{

   @Override
   public boolean install()
   {
      Project project = getFaceted();
      String topLevelPackage = project.getFacet(MetadataFacet.class).getTopLevelPackage();
      JavaClass testClass = JavaParser.create(JavaClass.class).setPackage(topLevelPackage);
      testClass.setName("AbstractTestCase").setAbstract(true);
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      try
      {
         facet.saveTestJavaSource(testClass.getEnclosingType());
      }
      catch (FileNotFoundException ffe)
      {
         ffe.printStackTrace();
         return false;
      }

      return true;
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean isInstalled()
   {
      return getFaceted().hasAllFacets(ForgeContainerAddonFacet.class);
   }

}
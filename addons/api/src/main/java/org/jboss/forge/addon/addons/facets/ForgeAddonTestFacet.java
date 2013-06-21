/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons.facets;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

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
public class ForgeAddonTestFacet extends AbstractForgeAddonFacet
{

   @Override
   public boolean install()
   {
      if (super.install())
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
            // this is not good :P
            ffe.printStackTrace();
         }

         return true;
      }
      return false;
   }

   @Override
   @SuppressWarnings("unchecked")
   protected List<Class<? extends ProjectFacet>> getRequiredFacets()
   {
      return Arrays.<Class<? extends ProjectFacet>> asList(JavaSourceFacet.class);
   }

}
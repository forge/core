/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui.setup;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.faces.FacesFacet;
import org.jboss.forge.addon.javaee.faces.ui.FacesSetupWizard;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.addon.ui.util.Selections;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FacesSetupWizardTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private UITestHarness uiTestHarness;

   @SuppressWarnings("unchecked")
   @Test
   public void testSetupCreatesFacesConfigXML() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, ServletFacet_3_1.class);
      try (CommandController tester = uiTestHarness.createCommandController(FacesSetupWizard.class,
               project.getRoot()))
      {
         tester.initialize();
         Assert.assertTrue(tester.isValid());

         Result result = tester.execute();
         project = Projects.getSelectedProject(projectFactory, Selections.from(project.getRoot()));
         Assert.assertFalse(result instanceof Failed);
         Assert.assertTrue(project.hasFacet(FacesFacet.class));
         Assert.assertTrue(project.getFacet(FacesFacet.class).getConfigFile().exists());
         Assert.assertTrue(project.getFacet(ServletFacet.class).getWebInfDirectory().exists());
         Assert.assertTrue(project.getFacet(ServletFacet.class).getWebInfDirectory().getChild("faces-config.xml")
                  .exists());
      }
   }
}

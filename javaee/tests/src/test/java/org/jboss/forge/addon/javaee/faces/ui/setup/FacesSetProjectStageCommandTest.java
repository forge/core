/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui.setup;

import javax.faces.application.ProjectStage;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.faces.FacesFacet_2_2;
import org.jboss.forge.addon.javaee.faces.ui.FacesSetProjectStageCommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class FacesSetProjectStageCommandTest
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
      return ShrinkWrap.create(AddonArchive.class).addBeansXML().addClass(ProjectHelper.class);
   }

   @Inject
   private ProjectHelper projectHelper;

   @Inject
   private UITestHarness uiTestHarness;

   @Test
   public void testSetProjectStage() throws Exception
   {
      Project project = projectHelper.createWebProject();
      projectHelper.installServlet_3_1(project);
      FacesFacet_2_2 facesFacet = projectHelper.installFaces_2_2(project);
      Assert.assertEquals(ProjectStage.Production, facesFacet.getProjectStage());
      try (CommandController controller = uiTestHarness.createCommandController(FacesSetProjectStageCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         Assert.assertTrue(controller.isEnabled());
         controller.setValueFor("stage", ProjectStage.Development);
         Assert.assertTrue(controller.canExecute());
         controller.execute();
         Assert.assertEquals(ProjectStage.Development, facesFacet.getProjectStage());
      }
   }
}

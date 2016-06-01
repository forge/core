/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ui;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.facets.JavaEE6Facet;
import org.jboss.forge.addon.javaee.facets.JavaEE7Facet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Result;
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
public class JavaEESetupCommandTest
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
   private UITestHarness uiTestHarness;

   @Inject
   private ProjectHelper projectHelper;

   @Inject
   private DependencyInstaller dependencyInstaller;

   private static final Dependency JAVAEE6 =
            DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0").setScopeType("import")
                     .setPackaging("pom").setVersion("3.0.2.Final");

   private static final Dependency JAVAEE7 =
            DependencyBuilder.create().setGroupId("javax").setArtifactId("javaee-api").setVersion("7.0")
                     .setScopeType("provided");

   @Test
   public void testJavaEE6Setup() throws Exception
   {
      Project project = projectHelper.createWebProject();
      try (CommandController tester = uiTestHarness
               .createCommandController(JavaEESetupCommand.class, project.getRoot()))
      {
         // Launch
         tester.initialize();

         Assert.assertTrue(tester.isValid());
         tester.setValueFor("javaEEVersion", "6");
         Assert.assertTrue(tester.isValid());

         Result result = tester.execute();
         Assert.assertEquals("JavaEE 6 has been installed.", result.getMessage());
      }
      project = projectHelper.refreshProject(project);
      Assert.assertTrue(project.hasFacet(JavaEE6Facet.class));
      Assert.assertTrue(dependencyInstaller.isInstalled(project, JAVAEE6));
      Assert.assertFalse(dependencyInstaller.isInstalled(project, JAVAEE7));
   }

   @Test
   public void testJavaEE7Setup() throws Exception
   {
      Project project = projectHelper.createWebProject();
      try (CommandController tester = uiTestHarness
               .createCommandController(JavaEESetupCommand.class, project.getRoot()))
      {
         // Launch
         tester.initialize();

         Assert.assertTrue(tester.isValid());
         tester.setValueFor("javaEEVersion", "7");
         Assert.assertTrue(tester.isValid());

         Result result = tester.execute();
         Assert.assertEquals("JavaEE 7 has been installed.", result.getMessage());
      }
      project = projectHelper.refreshProject(project);
      Assert.assertTrue(project.hasFacet(JavaEE7Facet.class));
      Assert.assertFalse(dependencyInstaller.isInstalled(project, JAVAEE6));
      Assert.assertFalse(dependencyInstaller.isManaged(project, JAVAEE6));
      Assert.assertTrue(dependencyInstaller.isInstalled(project, JAVAEE7));
   }

}

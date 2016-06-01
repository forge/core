/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class JavaNewPackageCommandTest
{
   @Inject
   private UITestHarness testHarness;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
   }

   @Test
   public void testCreatePackage() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController(JavaNewPackageCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "org.example.mynewpackage");
         controller.setValueFor("createPackageInfo", "true");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         DirectoryResource packageResource = facet.getPackage("org.example.mynewpackage");
         Assert.assertNotNull(packageResource);
         Assert.assertThat(packageResource.exists(), is(true));
         Assert.assertThat(packageResource.getChild("package-info.java").exists(), is(true));
      }
   }

   @Test
   public void testCreatePackageWithTilde() throws Exception
   {
      try (CommandController controller = testHarness.createCommandController(JavaNewPackageCommand.class,
               project.getRoot()))
      {
         controller.initialize();
         controller.setValueFor("named", "~.mynewpackagewithtilde");
         controller.setValueFor("createPackageInfo", "true");
         Assert.assertTrue(controller.isValid());
         Assert.assertTrue(controller.canExecute());
         Result result = controller.execute();
         Assert.assertThat(result, is(not(instanceOf(Failed.class))));

         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         DirectoryResource packageResource = facet.getPackage("unknown.mynewpackagewithtilde");
         Assert.assertNotNull(packageResource);
         Assert.assertThat(packageResource.exists(), is(true));
         Assert.assertThat(packageResource.getChild("package-info.java").exists(), is(true));
      }
   }
}

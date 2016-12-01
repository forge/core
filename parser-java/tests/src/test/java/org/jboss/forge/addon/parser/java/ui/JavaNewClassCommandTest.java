/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import static org.hamcrest.CoreMatchers.is;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JavaNewClassCommandTest
{

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:parser-java"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"));
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private UITestHarness testHarness;

   @Inject
   private FacetFactory facetFactory;

   private Project project;

   private CommandController commandController;

   @Before
   public void setup() throws Exception
   {
      createTempProject();
      createCommandController();
   }

   @Test
   public void testNewClass() throws Exception
   {
      commandController.initialize();
      setName("Foo");
      commandController.execute();
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getJavaResource(facet.getBasePackage() + ".Foo");
      Assert.assertThat(javaResource.exists(), is(true));
      Assert.assertThat(javaResource.getJavaType().isClass(), is(true));
   }

   @Test
   public void testNewClassExtends() throws Exception
   {
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      commandController.initialize();
      setName("Foo");
      setTargetPackage("org.foo");
      createClass("org.foo", "SuperClass");
      setExtends("org.foo.SuperClass");
      commandController.execute();
      JavaResource javaResource = facet.getJavaResource("org.foo.Foo");
      Assert.assertThat(javaResource.exists(), is(true));
      Assert.assertThat(javaResource.getJavaType().isClass(), is(true));
      JavaClassSource javaClass = javaResource.getJavaType();
      Assert.assertThat(javaClass.getSuperType(), is("org.foo.SuperClass"));
   }

   @After
   public void tearDown() throws Exception
   {
      commandController.close();
   }

   private JavaResource createClass(String packageName, String name) throws FileNotFoundException
   {
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaClassSource targetClass = Roaster.create(JavaClassSource.class).setName(name)
               .setPackage(packageName);
      return facet.saveJavaSource(targetClass);
   }

   private void createTempProject()
   {
      project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
   }

   private void createCommandController() throws Exception
   {
      commandController = testHarness.createCommandController(JavaNewClassCommand.class, project.getRoot());
   }

   private void setName(String name)
   {
      commandController.setValueFor("named", name);
   }

   private void setTargetPackage(String type)
   {
      commandController.setValueFor("targetPackage", type);
   }

   private void setExtends(String type)
   {
      commandController.setValueFor("extends", type);
   }
}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.util.Iterator;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.addons.facets.AddonTestFacet;
import org.jboss.forge.addon.addons.facets.FurnaceVersionFacet;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.manager.maven.addon.MavenAddonDependencyResolver;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class NewFurnaceTestCommandTest
{

   private ProjectFactory projectFactory;
   private FacetFactory facetFactory;
   private UITestHarness testHarness;
   private Furnace furnace;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      facetFactory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
      testHarness = SimpleContainer.getServices(getClass().getClassLoader(), UITestHarness.class).get();
      furnace = SimpleContainer.getFurnace(getClass().getClassLoader());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCreateTestClass() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      facetFactory.install(project, FurnaceVersionFacet.class);
      project.getFacet(FurnaceVersionFacet.class).setVersion(furnace.getVersion().toString());
      facetFactory.install(project, AddonTestFacet.class);

      CommandController controller = testHarness.createCommandController(NewFurnaceTestCommand.class,
               project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "MyTestCase");
      controller.setValueFor("packageName", "org.jboss.forge.test");
      controller.setValueFor("reuseProjectAddons", false);
      UISelectMany<AddonId> component = (UISelectMany<AddonId>) controller.getInputs().get("addonDependencies");
      UISelectOne<AddonId> furnaceContainer = (UISelectOne<AddonId>) controller.getInputs().get("furnaceContainer");
      AddonId funaceContainerAddonId = furnaceContainer.getValueChoices().iterator().next();
      controller.setValueFor("furnaceContainer", funaceContainerAddonId);
      Iterator<AddonId> dependencies = component.getValueChoices().iterator();
      AddonId addonDependency = null;
      while (dependencies.hasNext())
      {
         addonDependency = dependencies.next();
      }
      controller.setValueFor("addonDependencies", addonDependency);
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertFalse(result instanceof Failed);
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      {
         Dependency dependency = DependencyBuilder.create(funaceContainerAddonId.getName()).setVersion(
                  funaceContainerAddonId.getVersion().toString())
                  .setClassifier(MavenAddonDependencyResolver.FORGE_ADDON_CLASSIFIER).setScopeType("test");
         Assert.assertTrue(deps.hasEffectiveDependency(dependency));
      }
      {
         Dependency dependency = DependencyBuilder.create(addonDependency.getName()).setVersion(
                  addonDependency.getVersion().toString())
                  .setClassifier(MavenAddonDependencyResolver.FORGE_ADDON_CLASSIFIER).setScopeType("test");
         Assert.assertTrue(deps.hasEffectiveDependency(dependency));
      }
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getTestJavaResource("org.jboss.forge.test.MyTestCase");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClassSource.class)));
      Assert.assertFalse(javaResource.getJavaType().hasSyntaxErrors());

      Resource<?> finalArtifact = project.getFacet(PackagingFacet.class).getFinalArtifact();
      Assert.assertFalse(finalArtifact.exists());
      Assert.assertTrue(project.getFacet(PackagingFacet.class).createBuilder().runTests(false).build()
               .exists());
      Assert.assertTrue(finalArtifact.exists());
   }

   @Test
   public void testCreateTestClassWithoutDependencies() throws Exception
   {
      Project project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
      facetFactory.install(project, FurnaceVersionFacet.class);
      project.getFacet(FurnaceVersionFacet.class).setVersion(furnace.getVersion().toString());
      facetFactory.install(project, AddonTestFacet.class);

      CommandController controller = testHarness.createCommandController(NewFurnaceTestCommand.class,
               project.getRoot());
      controller.initialize();
      controller.setValueFor("named", "MyTestCase");
      controller.setValueFor("packageName", "org.jboss.forge.test");
      controller.setValueFor("reuseProjectAddons", true);
      Assert.assertTrue(controller.canExecute());
      Result result = controller.execute();
      Assert.assertFalse(result instanceof Failed);
      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
      JavaResource javaResource = facet.getTestJavaResource("org.jboss.forge.test.MyTestCase");
      Assert.assertNotNull(javaResource);
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClassSource.class)));
      Assert.assertFalse(javaResource.getJavaType().hasSyntaxErrors());
      JavaClassSource javaClass = javaResource.getJavaType();
      MethodSource<JavaClassSource> getDeploymentMethod = javaClass.getMethod("getDeployment");
      Assert.assertNotNull(getDeploymentMethod);
      Assert.assertNull(getDeploymentMethod.getAnnotation("AddonDependencies").getLiteralValue());
      Resource<?> finalArtifact = project.getFacet(PackagingFacet.class).getFinalArtifact();
      Assert.assertFalse(finalArtifact.exists());
      Assert.assertTrue(project.getFacet(PackagingFacet.class).createBuilder().runTests(false).build()
               .exists());
      Assert.assertTrue(finalArtifact.exists());
   }
}

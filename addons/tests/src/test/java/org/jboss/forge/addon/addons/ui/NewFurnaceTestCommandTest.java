/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.addons.ui;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
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
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.manager.maven.addon.MavenAddonDependencyResolver;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class NewFurnaceTestCommandTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:addons")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap.create(ForgeArchive.class).
               addBeansXML().
               addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:addons")
               );
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private UITestHarness testHarness;

   @Inject
   private Furnace furnace;

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
      Assert.assertThat(javaResource.getJavaType(), is(instanceOf(JavaClass.class)));
      Assert.assertFalse(javaResource.getJavaType().hasSyntaxErrors());

      Resource<?> finalArtifact = project.getFacet(PackagingFacet.class).getFinalArtifact();
      Assert.assertFalse(finalArtifact.exists());
      Assert.assertTrue(project.getFacet(PackagingFacet.class).createBuilder().runTests(false).build()
               .exists());
      Assert.assertTrue(finalArtifact.exists());
   }
}

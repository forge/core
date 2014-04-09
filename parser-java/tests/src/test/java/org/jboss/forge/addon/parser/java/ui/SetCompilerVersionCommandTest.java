package org.jboss.forge.addon.parser.java.ui;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet.CompilerVersion;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class SetCompilerVersionCommandTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );
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
      project = projectFactory.createTempProject();
      facetFactory.install(project, JavaCompilerFacet.class);
      assertDefaultVersions();
      commandController = testHarness.createCommandController(SetCompilerVersionCommand.class, project.getRoot());
   }

   private void assertDefaultVersions()
   {
      assertSourceVersion(JavaCompilerFacet.DEFAULT_COMPILER_VERSION);
      assertTargetVersion(JavaCompilerFacet.DEFAULT_COMPILER_VERSION);
   }

   @Test
   public void testSetBothVersions() throws Exception
   {
      commandController.initialize();
      commandController.setValueFor("sourceVersion", CompilerVersion.JAVA_1_6);
      commandController.setValueFor("targetVersion", CompilerVersion.JAVA_1_6);
      commandController.execute();
      assertSourceVersion(CompilerVersion.JAVA_1_6);
      assertTargetVersion(CompilerVersion.JAVA_1_6);
   }

   @Test
   public void testSetSourceVersionOnly() throws Exception
   {
      commandController.initialize();
      commandController.setValueFor("sourceVersion", CompilerVersion.JAVA_1_4);
      commandController.execute();
      assertSourceVersion(CompilerVersion.JAVA_1_4);
      assertTargetVersion(JavaCompilerFacet.DEFAULT_COMPILER_VERSION);
   }

   @Test
   public void testSetTargetVersionOnly() throws Exception
   {
      commandController.initialize();
      commandController.setValueFor("targetVersion", CompilerVersion.JAVA_1_8);
      commandController.execute();
      assertSourceVersion(JavaCompilerFacet.DEFAULT_COMPILER_VERSION);
      assertTargetVersion(CompilerVersion.JAVA_1_8);
   }

   @Test
   public void testSetSourceVersionHigherThanTargetVersion() throws Exception
   {
      commandController.initialize();
      commandController.setValueFor("sourceVersion", CompilerVersion.JAVA_1_5);
      commandController.setValueFor("targetVersion", CompilerVersion.JAVA_1_4);
      assertTrue(commandController.execute() instanceof Failed);
   }

   @Test
   public void testSetSourceVersionHigherThanDefaultTargetVersion() throws Exception
   {
      commandController.initialize();
      commandController.setValueFor("sourceVersion", CompilerVersion.JAVA_1_8);
      assertTrue(commandController.execute() instanceof Failed);
   }

   @Test
   public void testSetTargetVersionLowerThanDefaultSourceVersion() throws Exception
   {
      commandController.initialize();
      commandController.setValueFor("targetVersion", CompilerVersion.JAVA_1_3);
      assertTrue(commandController.execute() instanceof Failed);
   }

   private void assertSourceVersion(CompilerVersion version)
   {
      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      assertEquals(version.toString(), mavenFacet.getProperties().get("maven.compiler.source"));
   }

   private void assertTargetVersion(CompilerVersion versionString)
   {
      MavenFacet mavenFacet = project.getFacet(MavenFacet.class);
      assertEquals(versionString.toString(), mavenFacet.getProperties().get("maven.compiler.target"));
   }
}

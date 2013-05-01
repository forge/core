/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.maven.model.Build;
import org.apache.maven.model.PluginManagement;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.facets.exceptions.PluginNotFoundException;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.maven.util.ProjectModelTest;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
@RunWith(Arquillian.class)
public class MavenPluginFacetManagedPluginTest extends ProjectModelTest
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   private static Project testProject;
   
   private final static int TEST_PROJECT_NBR_OF_MANAGED_PLUGINS = 5;

   @Deployment
   public static JavaArchive createTestArchive()
   {
      return ProjectModelTest.createTestArchive().addAsManifestResource(
               "META-INF/services/org.jboss.forge.project.dependencies.DependencyResolverProvider");
   }

   @Before
   @Override
   public void before() throws IOException
   {

      project = null;
      super.before();

      if (testProject == null)
      {
         testProject = projectFactory.findProjectRecursively(ResourceUtil.getContextDirectory(resourceFactory
                  .getResourceFrom(new File("src/test/resources/test-pom-managed"))));
      }
   }

   @Test
   public void testIsInstalled() throws Exception
   {
      boolean isInstalled = testProject.hasFacet(MavenPluginFacet.class);
      assertEquals(true, isInstalled);
   }

   @Test
   public void testListManagedPlugins() throws Exception
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      List<MavenPlugin> mavenManagedPlugins = mavenPluginFacet.listConfiguredManagedPlugins();
      assertThat(mavenManagedPlugins.size(), is(TEST_PROJECT_NBR_OF_MANAGED_PLUGINS));
   }

   @Test
   public void testAddManagedPlugin() 
   {
      Project project = getProject();
      MavenPluginFacet mavenPluginFacet = getProject().getFacet(MavenPluginFacet.class);
      
      int nrOfManagedPlugins = getNumberOfManagedPlugins(project);
      MavenPluginBuilder plugin = MavenPluginBuilder.create().setDependency(
               DependencyBuilder.create().setGroupId("org.apache.maven.plugins").setArtifactId("maven-site-plugin")
                        .setVersion("3.0"));

      mavenPluginFacet.addManagedPlugin(plugin);

      assertThat(getNumberOfManagedPlugins(project), is(nrOfManagedPlugins + 1));
   }

   private int getNumberOfManagedPlugins(Project project) 
   {
	   MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);
	   Build build = mavenCoreFacet.getPOM().getBuild();
   		if (build != null) {
   			PluginManagement pluginManagement = build.getPluginManagement();
   			if (pluginManagement != null) {
   				return pluginManagement.getPlugins().size();
   			}
	   	}
	   return 0;
   }

   @Test
   public void testHasManagedPlugin()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      boolean hasManagedPlugin = mavenPluginFacet.hasManagedPlugin(DependencyBuilder
               .create("org.codehaus.mojo:findbugs-maven-plugin"));
      assertTrue(hasManagedPlugin);
   }

   @Test
   public void testHasManagedPluginForDefaultGroupId()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      boolean hasManagedPlugin = mavenPluginFacet.hasManagedPlugin(DependencyBuilder
               .create("org.apache.maven.plugins:maven-compiler-plugin"));
      assertTrue(hasManagedPlugin);
   }

   @Test
   public void testHasManagedPluginForNullGroupId()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      DependencyBuilder pluginDependency = DependencyBuilder.create().setArtifactId("maven-compiler-plugin");
      boolean hasManagedPlugin = mavenPluginFacet.hasManagedPlugin(pluginDependency);
      assertTrue(hasManagedPlugin);
   }

   @Test
   public void testHasManagedPluginForEmptyGroupId()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      DependencyBuilder pluginDependency = DependencyBuilder.create().setGroupId("")
               .setArtifactId("maven-compiler-plugin");
      boolean hasManagedPlugin = mavenPluginFacet.hasManagedPlugin(pluginDependency);
      assertTrue(hasManagedPlugin);
   }

   @Test
   public void testHasManagedPluginWhenPluginNotInstalled()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      boolean hasManagedPlugin = mavenPluginFacet.hasManagedPlugin(DependencyBuilder.create("test.plugins:fake"));
      assertFalse(hasManagedPlugin);
   }

   @Test
   public void testGetManagedPlugin()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      MavenPlugin plugin = mavenPluginFacet.getManagedPlugin(DependencyBuilder
               .create("org.codehaus.mojo:findbugs-maven-plugin"));
      assertNotNull(plugin);
      assertThat(plugin.getDependency().getArtifactId(), is("findbugs-maven-plugin"));
      assertThat(plugin.getDependency().getVersion(), is("2.3.2"));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetManagedPluginWhenNoneExistDoesNotThrowException() throws Exception
   {
      Project project = createProject(MavenCoreFacet.class, ResourceFacet.class, DependencyFacet.class,
               PackagingFacet.class);
      project.getFacet(MavenPluginFacet.class).listConfiguredManagedPlugins();
   }

   @Test(expected = PluginNotFoundException.class)
   public void testGetPluginException()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      mavenPluginFacet.getManagedPlugin(DependencyBuilder.create("test.plugins:fake"));
   }

   @Test
   public void testRemoveManagedPlugin()
   {
	  Project project = getProject();
      MavenPluginFacet mavenPluginFacet = getProject().getFacet(MavenPluginFacet.class);
      int nrOfManagedPlugins = getNumberOfManagedPlugins(project);
      
      // FIXME: we should not use the mavenPluginFacet to addManagedPlugin (break unit tests principles)
      // One way would be to add a pluginManagement to the project (getProject()), 
      // but I can not find out where is defined the pom for this project 
      MavenPluginBuilder plugin = MavenPluginBuilder.create().setDependency(
               DependencyBuilder.create().setGroupId("org.apache.maven.plugins").setArtifactId("maven-site-plugin")
                        .setVersion("3.0"));
      mavenPluginFacet.addManagedPlugin(plugin);
      assertThat(getNumberOfManagedPlugins(project), is(++nrOfManagedPlugins));
      
      mavenPluginFacet.removeManagedPlugin(DependencyBuilder.create("org.apache.maven.plugins:maven-site-plugin"));
	  
      
      assertThat(getNumberOfManagedPlugins(project), is(--nrOfManagedPlugins));
   }

   @Test
   public void testAddConfigurationToExistingManagedPlugin()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      MavenPlugin plugin = mavenPluginFacet.getManagedPlugin(DependencyBuilder
               .create("org.codehaus.mojo:findbugs-maven-plugin"));
      MavenPluginBuilder pluginBuilder = MavenPluginBuilder.create(plugin);

      pluginBuilder.createConfiguration().createConfigurationElement("xmlOutput").setText("true");

      assertEquals(
               "<plugin><groupId>org.codehaus.mojo</groupId><artifactId>findbugs-maven-plugin</artifactId><version>2.3.2</version><configuration><xmlOutput>true</xmlOutput></configuration></plugin>",
               pluginBuilder.toString());
   }

   @Test
   public void testAddConfigurationToExistingManagedPluginWithConfig()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      MavenPlugin plugin = mavenPluginFacet
               .getManagedPlugin(DependencyBuilder.create().setArtifactId("maven-compiler-plugin"));
      MavenPluginBuilder pluginBuilder = MavenPluginBuilder.create(plugin);

      pluginBuilder.createConfiguration().createConfigurationElement("testelement").setText("test");

      assertEquals(
               "<plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId><version>2.0.2</version><configuration><source>1.6</source><target>1.6</target><testelement>test</testelement></configuration></plugin>",
               pluginBuilder.toString());
   }
   
   
   
}

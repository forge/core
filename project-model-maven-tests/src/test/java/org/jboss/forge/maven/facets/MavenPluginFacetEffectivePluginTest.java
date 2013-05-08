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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.facets.exceptions.PluginNotFoundException;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ConfigurationImpl;
import org.jboss.forge.maven.plugins.Execution;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
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
 * @author <a href="mailto:salmon_charles@gmail.com">Charles-Edouard Salmon</a>
 */
@RunWith(Arquillian.class)
public class MavenPluginFacetEffectivePluginTest extends ProjectModelTest
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   private static Project testProject;

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
                  .getResourceFrom(new File("src/test/resources/test-pom-effective/child"))));
      }
   }

   @Test
   public void testIsInstalled() throws Exception
   {
      boolean isInstalled = testProject.hasFacet(MavenPluginFacet.class);
      assertEquals(true, isInstalled);
   }

   @Test
   public void testListEffectivePlugins() throws Exception
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class); 
      List<MavenPlugin> mavenPlugins = mavenPluginFacet.listConfiguredEffectivePlugins();
      assertThat(mavenPlugins.size(), is(3));
   }

   @Test
   public void testHasEffectivePlugin()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      boolean hasPlugin = mavenPluginFacet.hasEffectivePlugin(DependencyBuilder
               .create("org.codehaus.mojo:findbugs-maven-plugin"));
      assertTrue(hasPlugin);
   }

   @Test
   public void testGetEffectivePlugin()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      MavenPlugin plugin = mavenPluginFacet.getEffectivePlugin(DependencyBuilder
               .create("org.codehaus.mojo:findbugs-maven-plugin"));
      assertNotNull(plugin);
      assertThat(plugin.getDependency().getArtifactId(), is("findbugs-maven-plugin"));
      assertThat(plugin.getDependency().getVersion(), is("2.3.2"));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetEffectivePluginWhenNoneExistDoesNotThrowException() throws Exception
   {
      Project project = createProject(MavenCoreFacet.class, ResourceFacet.class, DependencyFacet.class,
               PackagingFacet.class);
      project.getFacet(MavenPluginFacet.class).listConfiguredEffectivePlugins();
   }

   @Test(expected = PluginNotFoundException.class)
   public void testGetEffectivePluginException()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      mavenPluginFacet.getEffectivePlugin(DependencyBuilder.create("test.plugins:fake"));
   }
   
   @Test
   public void testListEffectiveManagedPlugins() throws Exception
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      List<MavenPlugin> mavenManagedPlugins = mavenPluginFacet.listConfiguredEffectiveManagedPlugins();
      assertThat(mavenManagedPlugins.size(), is(9));
   }
   
   @Test
   public void testHasEffectiveManagedPlugin()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      boolean HasEffectiveManagedPlugin = mavenPluginFacet.hasEffectiveManagedPlugin(DependencyBuilder
               .create("org.codehaus.mojo:findbugs-maven-plugin"));
      assertTrue(HasEffectiveManagedPlugin);
   }

   @Test
   public void testHasEffectiveManagedPluginForDefaultGroupId()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      boolean HasEffectiveManagedPlugin = mavenPluginFacet.hasEffectiveManagedPlugin(DependencyBuilder
               .create("org.apache.maven.plugins:maven-compiler-plugin"));
      assertTrue(HasEffectiveManagedPlugin);
   }

   @Test
   public void testHasEffectiveManagedPluginForNullGroupId()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      DependencyBuilder pluginDependency = DependencyBuilder.create().setArtifactId("maven-compiler-plugin");
      boolean HasEffectiveManagedPlugin = mavenPluginFacet.hasEffectiveManagedPlugin(pluginDependency);
      assertTrue(HasEffectiveManagedPlugin);
   }

   @Test
   public void testHasEffectiveManagedPluginForEmptyGroupId()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      DependencyBuilder pluginDependency = DependencyBuilder.create().setGroupId("")
               .setArtifactId("maven-compiler-plugin");
      boolean HasEffectiveManagedPlugin = mavenPluginFacet.hasEffectiveManagedPlugin(pluginDependency);
      assertTrue(HasEffectiveManagedPlugin);
   }

   @Test
   public void testHasEffectiveManagedPluginWhenPluginNotInstalled()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      boolean HasEffectiveManagedPlugin = mavenPluginFacet.hasEffectiveManagedPlugin(DependencyBuilder.create("test.plugins:fake"));
      assertFalse(HasEffectiveManagedPlugin);
   }

   @Test
   public void testGetEffectiveManagedPlugin()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      MavenPlugin plugin = mavenPluginFacet.getEffectiveManagedPlugin(DependencyBuilder
               .create("org.codehaus.mojo:findbugs-maven-plugin"));
      assertNotNull(plugin);
      assertThat(plugin.getDependency().getArtifactId(), is("findbugs-maven-plugin"));
      assertThat(plugin.getDependency().getVersion(), is("2.3.2"));
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetEffectiveManagedPluginWhenNoneExistDoesNotThrowException() throws Exception
   {
      Project project = createProject(MavenCoreFacet.class, ResourceFacet.class, DependencyFacet.class,
               PackagingFacet.class);
      project.getFacet(MavenPluginFacet.class).listConfiguredEffectiveManagedPlugins();
   }

   @Test(expected = PluginNotFoundException.class)
   public void testGetEffectiveManagedPluginException()
   {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      mavenPluginFacet.getEffectiveManagedPlugin(DependencyBuilder.create("test.plugins:fake"));
   }
   
   @Test
   public void testGetMergedQooxdooMavenPlugin() {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      DependencyBuilder pluginDependency = DependencyBuilder.create().setGroupId("org.qooxdoo").setArtifactId("qooxdoo-maven-plugin");
      // Parent, pluginManagement section
      MavenPlugin managed = mavenPluginFacet.getEffectiveManagedPlugin(pluginDependency);
      assertNotNull(managed);
      assertThat(managed.getDependency().getVersion(), is("2.0-RC1"));
      assertNotNull(managed.getConfig());
      assertEquals("false", managed.getConfig().getConfigurationElement("useEmbeddedJython").getText().trim());
      // Child, plugin section
      MavenPlugin direct = mavenPluginFacet.getPlugin(pluginDependency);
      assertNotNull(direct);
      assertThat(direct.getDependency().getVersion(), is("2.2"));
      assertNotNull(direct.getConfig());
      assertEquals("true", direct.getConfig().getConfigurationElement("useEmbeddedJython").getText().trim());
      //Merge
      MavenPlugin merged = mavenPluginFacet.merge(direct, managed);
      // Compare
      assertNotNull(merged);
      assertThat(merged.getDependency().getVersion(), is("2.2"));
      assertThat(merged.getDependency().getArtifactId(), is("qooxdoo-maven-plugin"));
      assertThat(merged.getDependency().getGroupId(), is("org.qooxdoo"));
      // Config
      assertNotNull(merged.getConfig());
      assertThat(merged.getConfig().getConfigurationElement("useEmbeddedJython").getText().trim(),is("true"));
      assertThat(merged.getConfig().getConfigurationElement("testUnitBrowser").getText().trim(),is("phantomjs"));
      assertThat(merged.getConfig().getConfigurationElement("testUnitPhantomjsPath").getText().trim(),is("C:\\phantomjs-1.9.0\\phantomjs.exe"));
      // Executions
      assertThat(merged.listExecutions().size(),is(6));
      Map<String,Execution> exec = new HashMap<String,Execution>();
      for (Execution e: merged.listExecutions()) {
         exec.put(e.getId(), e);
      }
      // parent: <id>sdk-unpack</id><phase>initialize</phase><goals><goal>sdk-unpack</goal></goals>
      //                            <configuration><sdkParentDirectory>C:\temp\qooxdoo-sdk</sdkParentDirectory></configuration>
      assertNotNull(exec.get("sdk-unpack"));
      assertThat(exec.get("sdk-unpack").getPhase(),is("initialize"));
      assertThat(exec.get("sdk-unpack").getGoals().get(0),is("sdk-unpack"));
      assertThat(exec.get("sdk-unpack").getConfig().getConfigurationElement("sdkParentDirectory").getText().trim(),is("C:\\temp\\qooxdoo-sdk"));
      // parent: <id>generate-config</id><phase>generate-sources</phase><goals><goal>generate-config</goal></goals>
      assertNotNull(exec.get("generate-config"));
      assertThat(exec.get("generate-config").getPhase(),is("generate-sources"));
      assertThat(exec.get("generate-config").getGoals().get(0),is("generate-config"));
      // parent: <id>translation</id><phase>generate-resources</phase> <goals><goal>translation</goal></goals>
      // child: <id>translation</id><goals><goal>generate-html</goal></goals>
      assertNotNull(exec.get("translation"));
      assertThat(exec.get("translation").getPhase(),is("generate-resources"));
      assertThat(exec.get("translation").getGoals().size(),is(2));
      assertThat(exec.get("translation").getGoals().get(0),is("generate-html"));
      assertThat(exec.get("translation").getGoals().get(1),is("translation"));
      // parent: <id>generate-html</id> <phase>process-resources</phase><goals><goal>generate-html</goal></goals>
      // child: <id>generate-html</id><phase>process-sources</phase><goals><goal>generate-resources</goal></goals>
      assertNotNull(exec.get("generate-html"));
      assertThat(exec.get("generate-html").getPhase(),is("process-sources"));
      assertThat(exec.get("generate-html").getGoals().get(0),is("generate-resources"));
      // parent:  <id>compile</id> <phase>compile</phase><goals><goal>compile</goal></goals>
      //              <configuration><pythonInterpreter>C:\Python27\Python2.7.exe</pythonInterpreter>
      //                             <useEmbeddedJython>false</useEmbeddedJython></configuration>
      // child :  <id>compile</id><configuration> <useEmbeddedJython>true</useEmbeddedJython> </configuration>
      assertNotNull(exec.get("compile"));
      assertThat(exec.get("compile").getPhase(),is("compile"));
      assertThat(exec.get("compile").getGoals().get(0),is("compile"));
      assertThat(exec.get("compile").getConfig().getConfigurationElement("pythonInterpreter").getText().trim(),is("C:\\Python27\\Python2.7.exe"));
      assertThat(exec.get("compile").getConfig().getConfigurationElement("useEmbeddedJython").getText().trim(),is("true"));
      //child: <id>test-compile</id><phase>test-compile</phase><goals><goal>test-compile</goal></goals>
      //       <configuration><testUnitBrowser>firefox</testUnitBrowser></configuration>
      assertNotNull(exec.get("test-compile"));
      assertThat(exec.get("test-compile").getPhase(),is("test-compile"));
      assertThat(exec.get("test-compile").getGoals().get(0),is("test-compile"));
      assertThat(exec.get("test-compile").getConfig().getConfigurationElement("testUnitBrowser").getText().trim(),is("firefox"));

   }
   
   @Test
   // Warn: assumes that the merge method works (see testGetMergedQooxdooMavenPlugin above) 
   public void testGetResolvedQooxdooMavenPlugin() {
      MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
      DependencyBuilder pluginDependency = DependencyBuilder.create().setGroupId("org.qooxdoo").setArtifactId("qooxdoo-maven-plugin");
      // Parent, pluginManagement section
      MavenPlugin managed = mavenPluginFacet.getEffectiveManagedPlugin(pluginDependency);
      // Child, plugin section
      MavenPlugin direct = mavenPluginFacet.getPlugin(pluginDependency);
      //Merge
      MavenPlugin mergedRef = mavenPluginFacet.merge(direct, managed);
      MavenPlugin mergedChg = mavenPluginFacet.getEffectivePlugin(pluginDependency);
      assertPluginsEquals(mergedRef,mergedChg);
   }
   
   private void assertPluginsEquals(MavenPlugin ref, MavenPlugin chg) {
      assertNotNull(ref);
      assertNotNull(chg);
      assertEquals(ref.getDependency().getArtifactId(),chg.getDependency().getArtifactId());
      assertEquals(ref.getDependency().getGroupId(),chg.getDependency().getGroupId());
      assertEquals(ref.getDependency().getVersion(),chg.getDependency().getVersion());
      assertEquals(ref.isExtensionsEnabled(),chg.isExtensionsEnabled());
      // Config
      Map<String, String> cfgElmtsRefMap = new HashMap<String,String>();
      if (ref.getConfig() != null || chg.getConfig() != null) {
         assertNotNull(ref.getConfig());
         assertNotNull(chg.getConfig());
         assertEquals(ref.getConfig().listConfigurationElements().size(), chg.getConfig().listConfigurationElements().size());
         for (ConfigurationElement e: ref.getConfig().listConfigurationElements()) {
            cfgElmtsRefMap.put(e.getName(), e.toString());
         }
         for (ConfigurationElement e: chg.getConfig().listConfigurationElements()) {
            assertNotNull(cfgElmtsRefMap.get(e.getName()));
            assertEquals(cfgElmtsRefMap.get(e.getName()), e.toString());
         }
      }
      // Executions
      Map<String, PluginExecution> refExec = new MavenPluginAdapter(ref).getExecutionsAsMap();
      Map<String, PluginExecution> chgExec = new MavenPluginAdapter(chg).getExecutionsAsMap();
      if (refExec != null || chgExec != null) {
         assertNotNull(refExec);
         assertNotNull(chgExec);
         for (Map.Entry<String, PluginExecution> entry : refExec.entrySet()) { 
            PluginExecution pluginExecutionRef = entry.getValue();
            PluginExecution pluginExecutionChg = chgExec.get(entry.getKey());
            assertEquals(pluginExecutionRef.getId(), pluginExecutionChg.getId());
            assertEquals(pluginExecutionRef.getPhase(), pluginExecutionChg.getPhase());
            // Goals
            assertEquals(pluginExecutionRef.getGoals().size(), pluginExecutionChg.getGoals().size());
            Map<String, Boolean> hasGoals = new HashMap<String,Boolean>();
            for (String goal : pluginExecutionRef.getGoals()) {
               hasGoals.put(goal,new Boolean(true));
            }
            for (String goal : pluginExecutionChg.getGoals()) {
               assertTrue(hasGoals.get(goal));
            }
            // Configurations
            Map<String, String> cfgExecElmtsRefMap = new HashMap<String,String>();
            if (pluginExecutionRef.getConfiguration() != null || pluginExecutionChg.getConfiguration() != null) {
               Configuration pluginExecutionRefCfg = new ConfigurationImpl((Xpp3Dom) pluginExecutionRef.getConfiguration());
               Configuration pluginExecutionChgCfg = new ConfigurationImpl((Xpp3Dom) pluginExecutionChg.getConfiguration());
               assertNotNull(pluginExecutionRefCfg);
               assertNotNull(pluginExecutionChgCfg);
               assertEquals(pluginExecutionRefCfg.listConfigurationElements().size(), pluginExecutionChgCfg.listConfigurationElements().size());
               for (ConfigurationElement e: pluginExecutionRefCfg.listConfigurationElements()) {
                  cfgExecElmtsRefMap.put(e.getName(), e.toString());
               }
               for (ConfigurationElement e: pluginExecutionChgCfg.listConfigurationElements()) {
                  assertNotNull(cfgExecElmtsRefMap.get(e.getName()));
                  assertEquals(cfgExecElmtsRefMap.get(e.getName()), e.toString());
               }
            }
         }
      }
      
   }
   
   


  
}

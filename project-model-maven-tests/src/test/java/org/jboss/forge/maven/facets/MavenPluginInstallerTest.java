/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.maven.plugins.MavenPluginInstaller;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class MavenPluginInstallerTest extends AbstractShellTest
{

   @Inject
   private MavenPluginInstaller installer;

   @Test
   public void testInstall() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
      MavenPluginAdapter pluginToInstall = new MavenPluginAdapter(createMavenSitePlugin());
      Dependency dependency = pluginToInstall.getDependency();
      
      installer.install(project, pluginToInstall);

      Assert.assertTrue(plugins.hasPlugin(dependency));
      Assert.assertTrue(plugins.hasManagedPlugin(dependency));
      Assert.assertEquals("3.0", plugins.getManagedPlugin(dependency).getDependency().getVersion());
      MavenPlugin directPlugin = plugins.getPlugin(dependency);
      Assert.assertNull(directPlugin.getDependency().getVersion());
      Assert.assertEquals("org.apache.maven.plugins",directPlugin.getDependency().getGroupId());
      Assert.assertEquals("maven-site-plugin",directPlugin.getDependency().getArtifactId());
      assertPluginsConfigurationsEquals(pluginToInstall, directPlugin);
   }
   
   @Test
   public void testInstallMerge() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
      MavenPluginAdapter pluginToInstall = new MavenPluginAdapter(createMavenSitePlugin());
      Dependency dependency = pluginToInstall.getDependency();
      
      installer.install(project, pluginToInstall);
      Assert.assertTrue(plugins.hasPlugin(dependency));
      assertPluginsConfigurationsEquals(pluginToInstall, plugins.getPlugin(dependency));
      
      // Change the version and remove the configuration
      MavenPluginAdapter newPluginToInstall = new MavenPluginAdapter((MavenPlugin)pluginToInstall);
      newPluginToInstall.setVersion("3.1");
      removePluginConfiguration(newPluginToInstall);
      
      // Re-install with merge
      installer.setMergeWithExisting(true);  // should force to keep the configuration
      installer.install(project, newPluginToInstall);
      assertPluginsConfigurationsEquals(pluginToInstall, plugins.getPlugin(dependency));
      Assert.assertEquals("3.1", plugins.getManagedPlugin(dependency).getDependency().getVersion());
      
      
      // Re-install without merge
      installer.setMergeWithExisting(false);  // should force to delete the configuration
      installer.install(project, newPluginToInstall);
      Assert.assertEquals(0, plugins.getPlugin(dependency).getConfig().listConfigurationElements().size());
      Assert.assertEquals("3.1", plugins.getManagedPlugin(dependency).getDependency().getVersion());

   }
   
   @Test
   public void testInstallMergeUpdateConfig() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
      MavenPluginAdapter pluginToInstall = new MavenPluginAdapter(createMavenSitePlugin());
      Dependency dependency = pluginToInstall.getDependency();
      
      // First, install the plugin management 
      installer.installManaged(project, pluginToInstall);
      Assert.assertTrue(plugins.hasManagedPlugin(dependency));
      assertPluginsConfigurationsEquals(pluginToInstall, plugins.getManagedPlugin(dependency));
      
      // And the plugin, with the configuration kept in the plugin-management section
      installer.setMergeWithExisting(true); 
      installer.install(project, pluginToInstall);
      Assert.assertEquals(0, plugins.getPlugin(dependency).getConfig().listConfigurationElements().size());

      // Change the configuration (the version of the plugin findbugs-maven-plugin)
      MavenPluginAdapter newPluginToInstall = changePluginConfiguration(pluginToInstall);
      
      // Re-install the plugin
      // Should now have the new reportPlugin configuration defined in the plugin section,
      // while the generateReports should remains in the plugin-management section
      installer.setMergeWithExisting(true); // keep equals properties in management section
      installer.install(project, newPluginToInstall);
      Configuration plufinConfiguration = plugins.getPlugin(dependency).getConfig();
      Assert.assertEquals(1, plufinConfiguration.listConfigurationElements().size());
      Assert.assertTrue(plufinConfiguration.hasConfigurationElement("reportPlugins"));
      Assert.assertEquals("<reportPlugins><plugin><groupId>org.codehaus.mojo</groupId><artifactId>findbugs-maven-plugin</artifactId><version>2.4</version></plugin></reportPlugins>",plufinConfiguration.getConfigurationElement("reportPlugins").toString()); 
   }
   

   
   @Test
   public void testInstallManaged() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
      MavenPluginAdapter pluginToInstall = new MavenPluginAdapter(createMavenSitePlugin());
      Dependency dependency = pluginToInstall.getDependency();

      installer.installManaged(project, pluginToInstall);

      Assert.assertFalse(plugins.hasEffectivePlugin(dependency));
      Assert.assertTrue(plugins.hasManagedPlugin(dependency));
      Assert.assertEquals("3.0", plugins.getManagedPlugin(dependency).getDependency().getVersion());
      assertPluginsConfigurationsEquals(pluginToInstall, plugins.getManagedPlugin(dependency));
   }
   
   @Test
   public void testInstallManagedMerge() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);
      MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
      MavenPluginAdapter pluginToInstall = new MavenPluginAdapter(createMavenSitePlugin());
      Dependency dependency = pluginToInstall.getDependency();

      installer.installManaged(project, pluginToInstall);
      Assert.assertTrue(plugins.hasManagedPlugin(dependency));
      Assert.assertFalse(plugins.hasPlugin(dependency));
      assertPluginsConfigurationsEquals(pluginToInstall, plugins.getManagedPlugin(dependency));
      
      // Change the version and remove the configuration
      MavenPluginAdapter newPluginToInstall = new MavenPluginAdapter((MavenPlugin)pluginToInstall);
      newPluginToInstall.setVersion("3.1");
      removePluginConfiguration(newPluginToInstall);
      
      // Re-install with merge
      installer.setMergeWithExisting(true);  // should force to keep the configuration
      installer.installManaged(project, newPluginToInstall);
      assertPluginsConfigurationsEquals(pluginToInstall, plugins.getManagedPlugin(dependency));
      Assert.assertEquals("3.1", plugins.getManagedPlugin(dependency).getDependency().getVersion());
      
      // Re-install without merge
      installer.setMergeWithExisting(false);  // should force to delete the configuration
      installer.installManaged(project, newPluginToInstall);
      Assert.assertEquals(0, plugins.getManagedPlugin(dependency).getConfig().listConfigurationElements().size());
      Assert.assertEquals("3.1", plugins.getManagedPlugin(dependency).getDependency().getVersion());
   }
   
   @Test
   public void testInstallManagedChild() throws Exception
   {
      Project project = initializeProjectWithChild();
      MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
      MavenPluginAdapter pluginToInstall = new MavenPluginAdapter(createMavenSitePlugin());
      Dependency dependency = pluginToInstall.getDependency();
      
      // Make sure project is builded as expected
      Assert.assertTrue(plugins.hasEffectiveManagedPlugin(dependency));
      assertPluginsConfigurationsEquals(pluginToInstall, plugins.getEffectiveManagedPlugin(dependency));
      Assert.assertTrue(plugins.hasEffectivePlugin(dependency));
      Assert.assertEquals(1,plugins.getEffectivePlugin(dependency).listExecutions().size());
      
      // Install the dependency as a plugin-management
      // As the plugin is defined exactly the same in the hierarchy, 
      // the installed managed plugin should have only the artifactId defined
      installer.setMergeWithExisting(false);
      installer.installManaged(project, pluginToInstall);
      Assert.assertTrue(plugins.hasManagedPlugin(dependency));
      MavenPluginAdapter installedManagedPlugin = new MavenPluginAdapter(plugins.getManagedPlugin(dependency));
      Assert.assertEquals(0,installedManagedPlugin.getConfig().listConfigurationElements().size());
      Assert.assertNull(installedManagedPlugin.getDependency().getVersion());
      
      installer.setMergeWithExisting(true);  // In that case, the merge should not change anything
      installer.installManaged(project, pluginToInstall);
      Assert.assertTrue(plugins.hasManagedPlugin(dependency));
      installedManagedPlugin = new MavenPluginAdapter(plugins.getManagedPlugin(dependency));
      Assert.assertEquals(0,installedManagedPlugin.getConfig().listConfigurationElements().size());
      Assert.assertNull(installedManagedPlugin.getDependency().getVersion());
      
      // Add plugin-management the same execution as defined in the plugin section of the parent
      // Should append the execution in the plugin-management of the child, because the execution is not defined in the plugin-management of parent
      MavenPluginAdapter effectivePlugin =  new MavenPluginAdapter(plugins.getEffectivePlugin(dependency));
      PluginExecution pluginExecution = new PluginExecution();
      pluginExecution.setId(effectivePlugin.getExecutions().get(0).getId());
      pluginExecution.setPhase(effectivePlugin.getExecutions().get(0).getPhase());
      pluginExecution.setGoals(effectivePlugin.getExecutions().get(0).getGoals());
      pluginExecution.setConfiguration(effectivePlugin.getExecutions().get(0).getConfiguration());
      pluginToInstall.addExecution(pluginExecution);
      installer.installManaged(project, pluginToInstall);
      installedManagedPlugin = new MavenPluginAdapter(plugins.getManagedPlugin(dependency));
      Assert.assertEquals(1,installedManagedPlugin.listExecutions().size());
      Assert.assertEquals(effectivePlugin.getExecutions().get(0).getId(), installedManagedPlugin.getExecutions().get(0).getId());
      Assert.assertEquals(effectivePlugin.getExecutions().get(0).getPhase(), installedManagedPlugin.getExecutions().get(0).getPhase());
      Assert.assertEquals(effectivePlugin.getExecutions().get(0).getGoals(), installedManagedPlugin.getExecutions().get(0).getGoals());
      Assert.assertEquals(effectivePlugin.getExecutions().get(0).getConfiguration(), installedManagedPlugin.getExecutions().get(0).getConfiguration());
      
   }
   
   @Test
   public void testInstallChild() throws Exception
   {
      Project project = initializeProjectWithChild();
      MavenPluginFacet plugins = project.getFacet(MavenPluginFacet.class);
      MavenPluginAdapter pluginToInstall = new MavenPluginAdapter(createMavenSitePlugin());
      Dependency dependency = pluginToInstall.getDependency();
      
      // Make sure project is builded as expected
      Assert.assertTrue(plugins.hasEffectiveManagedPlugin(dependency));
      assertPluginsConfigurationsEquals(pluginToInstall, plugins.getEffectiveManagedPlugin(dependency));
      Assert.assertTrue(plugins.hasEffectivePlugin(dependency));
      Assert.assertEquals(1,plugins.getEffectivePlugin(dependency).listExecutions().size());
      
      // Install the dependency as a plugin
      // As the plugin is defined exactly the same in the hierarchy, 
      // the installed managed plugin should have only the artifactId defined
      // while the installed plugin will only have the artifactId
      installer.setMergeWithExisting(false);
      installer.install(project, pluginToInstall);
      Assert.assertTrue(plugins.hasManagedPlugin(dependency));
      MavenPluginAdapter installedManagedPlugin = new MavenPluginAdapter(plugins.getManagedPlugin(dependency));
      Assert.assertEquals(0,installedManagedPlugin.getConfig().listConfigurationElements().size());
      Assert.assertEquals("3.0",installedManagedPlugin.getDependency().getVersion());
      Assert.assertTrue(plugins.hasPlugin(dependency));
      MavenPluginAdapter installedPlugin = new MavenPluginAdapter(plugins.getPlugin(dependency));
      Assert.assertEquals(0,installedPlugin.getConfig().listConfigurationElements().size());
      Assert.assertNull(installedPlugin.getDependency().getVersion());
      
      // Add plugin-management the same execution as defined in the plugin section of the parent
      // Because the execution is defined in the plugin parent, only the id of the execution should be installed into direct pom
      // The rest of the execution configuration should be left in parent
      MavenPluginAdapter effectivePlugin =  new MavenPluginAdapter(plugins.getEffectivePlugin(dependency));
      PluginExecution pluginExecution = new PluginExecution();
      pluginExecution.setId(effectivePlugin.getExecutions().get(0).getId());
      pluginExecution.setPhase(effectivePlugin.getExecutions().get(0).getPhase());
      pluginExecution.setGoals(effectivePlugin.getExecutions().get(0).getGoals());
      pluginExecution.setConfiguration(effectivePlugin.getExecutions().get(0).getConfiguration());
      pluginToInstall.addExecution(pluginExecution);
      // And change configuration
      pluginToInstall = changePluginConfiguration(pluginToInstall);
      installer.install(project, pluginToInstall);
      installedPlugin = new MavenPluginAdapter(plugins.getPlugin(dependency));
      effectivePlugin =  new MavenPluginAdapter(plugins.getEffectivePlugin(dependency));
      // Execution, direct vs effective
      Assert.assertEquals(1,effectivePlugin.listExecutions().size());
      Assert.assertEquals(1,installedPlugin.listExecutions().size());
      // Id
      Assert.assertEquals("test-site",effectivePlugin.getExecutions().get(0).getId());
      Assert.assertEquals("test-site", installedPlugin.getExecutions().get(0).getId());
      // Phase
      Assert.assertEquals("post-site",effectivePlugin.getExecutions().get(0).getPhase());
      Assert.assertNull(installedPlugin.getExecutions().get(0).getPhase());
      // Goals
      Assert.assertEquals(1, effectivePlugin.getExecutions().get(0).getGoals().size());
      Assert.assertEquals("run", effectivePlugin.getExecutions().get(0).getGoals().get(0));
      Assert.assertEquals(0,installedPlugin.getExecutions().get(0).getGoals().size());
      // configuration
      Assert.assertNull(installedPlugin.getExecutions().get(0).getConfiguration());
      // Check configuration
      Assert.assertEquals(1, installedPlugin.getConfig().listConfigurationElements().size());
      Assert.assertEquals("<reportPlugins><plugin><groupId>org.codehaus.mojo</groupId><artifactId>findbugs-maven-plugin</artifactId><version>2.4</version></plugin></reportPlugins>",installedPlugin.getConfig().getConfigurationElement("reportPlugins").toString()); 

   }
   
   
   private void assertPluginsConfigurationsEquals(MavenPlugin ref, MavenPlugin chg) {
      assertNotNull(ref);
      assertNotNull(chg);
      // Config
      Map<String, String> cfgElmtsRefMap = new HashMap<String,String>();
      if (ref.getConfig() != null || chg.getConfig() != null) {
         assertNotNull(ref.getConfig());
         assertNotNull(chg.getConfig());
         assertEquals(ref.getConfig().listConfigurationElements().size(), chg.getConfig().listConfigurationElements().size());
         for (ConfigurationElement e: ref.getConfig().listConfigurationElements()) {
            cfgElmtsRefMap.put(e.getName(), e.getText() == null ? "" : e.getText().trim());
         }
         for (ConfigurationElement e: chg.getConfig().listConfigurationElements()) {
            assertNotNull(cfgElmtsRefMap.get(e.getName()));
            assertEquals(cfgElmtsRefMap.get(e.getName()), e.getText() == null ? "" : e.getText().trim());
         }
      }
   }

   private Plugin createMavenSitePlugin() throws Exception {
      Plugin plugin = createPlugin("org.apache.maven.plugins","maven-site-plugin","3.0");
      String mavenSitePluginCfg = 
               "<configuration>" +
               "   <reportPlugins>" +
               "       <plugin>" +
               "           <groupId>org.codehaus.mojo</groupId>" +
               "           <artifactId>findbugs-maven-plugin</artifactId>" +
               "           <version>2.3.2</version>" +
               "       </plugin>" +
               "   </reportPlugins>" +
               "   <generateReports>true</generateReports>" +
               "</configuration>";
      Xpp3Dom dom;
      dom = Xpp3DomBuilder.build(
              new ByteArrayInputStream(mavenSitePluginCfg.getBytes()),
              "UTF-8");

      plugin.setConfiguration(dom);
      return plugin;
  }
   
  public void  removePluginConfiguration(MavenPluginAdapter plugin) throws Exception {
      Xpp3Dom dom = Xpp3DomBuilder.build(
              new ByteArrayInputStream("<configuration></configuration>".getBytes()), "UTF-8");

      plugin.setConfiguration(dom);
   }
  
  public MavenPluginAdapter changePluginConfiguration(MavenPlugin pluginToInstall) throws Exception {
     MavenPluginAdapter newPluginToInstall = new MavenPluginAdapter((MavenPlugin)pluginToInstall);
     String mavenSitePluginCfg = 
              "<configuration>" +
              "   <reportPlugins>" +
              "       <plugin>" +
              "           <groupId>org.codehaus.mojo</groupId>" +
              "           <artifactId>findbugs-maven-plugin</artifactId>" +
              "           <version>2.4</version>" +
              "       </plugin>" +
              "   </reportPlugins>" +
              "   <generateReports>true</generateReports>" +
              "</configuration>";
     Xpp3Dom dom;
     dom = Xpp3DomBuilder.build(
             new ByteArrayInputStream(mavenSitePluginCfg.getBytes()),
             "UTF-8");
     newPluginToInstall.setConfiguration(dom);
     return newPluginToInstall;
  }

   private Plugin createPlugin(String groupId, String artifactId, String version)
   {
      Plugin plugin = new Plugin();
      plugin.setGroupId(groupId);
      plugin.setArtifactId(artifactId);
      plugin.setVersion(version);

      return plugin;
   }
   
   private void debugPom(Project project) {
      Resource pom = project.getProjectRoot().getChild("pom.xml");
      System.out.println(pom.getFullyQualifiedName());
      try {
         BufferedReader in = new BufferedReader(new InputStreamReader(pom.getResourceInputStream()));
         String line = null;
         while((line = in.readLine()) != null) {
            System.out.println(line);
         }
      } catch (Exception e) {
         System.out.println("Error while trying to read pom file:"+e);
      }
   }
   
   protected Project initializeProjectWithChild() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      queueInputLines("");
      getShell().execute("new-project --named test --topLevelPackage com.test --type pom");
      // Alter pom to add a plugin management
      Resource pom = getProject().getProjectRoot().getChild("pom.xml");
      StringBuffer pomStr = new StringBuffer();
      try {
         BufferedReader in = new BufferedReader(new InputStreamReader(pom.getResourceInputStream()));
         String line = null;
         while((line = in.readLine()) != null) {
            if (line.matches(".*<build>.*")) {
               line +=
                    "<pluginManagement>"+
                     "<plugins>"+
                        "<plugin>"+
                          "<artifactId>maven-site-plugin</artifactId>"+
                          "<version>3.0</version>"+
                          "<configuration>"+
                            "<reportPlugins>"+
                              "<plugin>"+
                                "<groupId>org.codehaus.mojo</groupId>"+
                                "<artifactId>findbugs-maven-plugin</artifactId>"+
                                "<version>2.3.2</version>"+
                              "</plugin>"+
                            "</reportPlugins>"+
                            "<generateReports>true</generateReports>"+
                          "</configuration>"+
                        "</plugin>"+
                      "</plugins>"+
                    "</pluginManagement>"+
                    "<plugins>"+
                      "<plugin>"+
                        "<artifactId>maven-site-plugin</artifactId>"+
                        "<executions>"+
                            "<execution>"+
                              "<id>test-site</id>"+
                              "<phase>post-site</phase>"+
                              "<goals>"+
                                "<goal>run</goal>"+
                              "</goals>"+
                              "<configuration>"+
                               "<tempWebappDirectory>/tmp</tempWebappDirectory>"+
                              "</configuration>"+
                            "</execution>"+
                         "</executions>"+     
                      "</plugin>"+
                    "</plugins>";
            }
            pomStr.append(line+"\n");
         }
      } catch (Exception e) {
         System.out.println("Error while trying to read pom file:"+e);
      }
      
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pom.getFullyQualifiedName()), false));
         bw.write(pomStr.toString());
         bw.close();
       } 
      catch (Exception e) {
         System.out.println("Error while trying to write pom file:"+e);
       }
      
      queueInputLines("Y","Y","Y");
      getShell().execute("new-project --named testchild --type pom");
      return getProject();
   }
   

}

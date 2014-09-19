package org.jboss.forge.addon.configuration.ui;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConfigCommandTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:configuration"),
            @AddonDependency(name = "org.jboss.forge.addon:shell"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:configuration"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell")
               );

      return archive;
   }

   @Inject
   private Configuration userConfig;

   @Inject
   private ShellTest test;

   @Inject
   private ProjectFactory projectFactory;

   @Before
   public void setUp() throws Exception
   {
      test.clearScreen();
   }

   @Test
   public void testConfigList() throws Exception
   {
      addPropsToUserConfig();
      test.execute("config-list", 5, TimeUnit.SECONDS);
      Assert.assertThat(test.getStdOut(), containsString("key1=user: [userValue1]"));
      Assert.assertThat(test.getStdOut(), containsString("key2=user: [userValue2]"));
   }

   @Test
   public void testConfigSetProperty() throws Exception
   {
      Assert.assertFalse(test.execute("config-set --key key1 --value userValue1", 5, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertFalse(test.execute("config-list", 5, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertThat(test.getStdOut(), containsString("key1=user: [userValue1]"));
   }

   @Test
   public void testConfigListInProject() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Configuration projectConfig = project.getFacet(ConfigurationFacet.class).getConfiguration();
      addPropsToProjectConfig(projectConfig);
      test.getShell().setCurrentResource(project.getRoot());
      test.execute("config-list", 5, TimeUnit.SECONDS);
      Assert.assertThat(test.getStdOut(), containsString("key2=project: [projectValue2]"));
      Assert.assertThat(test.getStdOut(), containsString("key3=project: [projectValue3]"));
   }

   @Test
   public void testConfigClear() throws Exception
   {
      Assert.assertFalse(test.execute("config-set --key key1 --value userValue1", 5, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertFalse(test.execute("config-list", 5, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertThat(test.getStdOut(), containsString("key1=user: [userValue1]"));
      test.clearScreen();
      Assert.assertFalse(test.execute("config-clear --key key1", 5, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertFalse(test.execute("config-list", 5, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertThat(test.getStdOut(), not(containsString("key1=user: [userValue1]")));
   }

   @Test
   public void testConfigSetPropertyListInProject() throws Exception
   {
      Project project = projectFactory.createTempProject();
      test.getShell().setCurrentResource(project.getRoot());
      test.execute("config-set --key key2 --value projectValue2 --local", 5, TimeUnit.MINUTES);
      test.execute("config-set --key key3 --value projectValue3 --local", 5, TimeUnit.SECONDS);
      Assert.assertFalse(test.execute("config-list", 5, TimeUnit.SECONDS) instanceof Failed);
      Assert.assertThat(test.getStdOut(), containsString("key2=project: [projectValue2]"));
      Assert.assertThat(test.getStdOut(), containsString("key3=project: [projectValue3]"));
   }

   @Test
   public void testMergedConfigList() throws Exception
   {
      addPropsToUserConfig();
      Project project = projectFactory.createTempProject();
      Configuration projectConfig = project.getFacet(ConfigurationFacet.class).getConfiguration();
      addPropsToProjectConfig(projectConfig);
      test.getShell().setCurrentResource(project.getRoot());
      test.execute("config-list", 5, TimeUnit.SECONDS);
      Assert.assertThat(test.getStdOut(), containsString("key1=user: [userValue1]"));
      Assert.assertThat(test.getStdOut(), containsString("key2=user: [userValue2], project: [projectValue2]"));
      Assert.assertThat(test.getStdOut(), containsString("key3=project: [projectValue3]"));
   }

   private void addPropsToUserConfig()
   {
      userConfig.setProperty("key1", "userValue1");
      userConfig.setProperty("key2", "userValue2");
   }

   private void addPropsToProjectConfig(Configuration projectConfig)
   {
      projectConfig.addProperty("key2", "projectValue2");
      projectConfig.addProperty("key3", "projectValue3");
   }

   @After
   public void tearDown() throws Exception
   {
      userConfig.clearProperty("key1");
      userConfig.clearProperty("key2");
   }
}
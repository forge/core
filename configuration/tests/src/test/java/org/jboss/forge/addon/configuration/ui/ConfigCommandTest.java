/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration.ui;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.Subset;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConfigCommandTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:configuration"),
            @AddonDependency(name = "org.jboss.forge.addon:shell"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Inject
   private Configuration userConfig;

   @Inject
   private ShellTest test;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @Subset("subset.subset")
   private Configuration subSubsetConfiguration;

   @Before
   public void setUp() throws Exception
   {
      test.clearScreen();
   }

   @Test
   public void testConfigList() throws Exception
   {
      addPropsToUserConfig();
      test.execute("config-list", 15, TimeUnit.SECONDS);
      assertThat(test.getStdOut(), containsString("key1=user: [userValue1]"));
      assertThat(test.getStdOut(), containsString("key2=user: [userValue2]"));
   }

   @Test
   public void testConfigSetProperty() throws Exception
   {
      assertFalse(test.execute("config-set --key key1 --value userValue1", 15, TimeUnit.SECONDS) instanceof Failed);
      test.clearScreen();
      assertFalse(test.execute("config-list", 15, TimeUnit.SECONDS) instanceof Failed);
      assertThat(test.getStdOut(), containsString("key1=user: [userValue1]"));
   }

   @Test
   public void testConfigListInProject() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Configuration projectConfig = project.getFacet(ConfigurationFacet.class).getConfiguration();
      addPropsToProjectConfig(projectConfig);
      test.getShell().setCurrentResource(project.getRoot());
      test.execute("config-list", 15, TimeUnit.SECONDS);
      assertThat(test.getStdOut(), containsString("key2=project: [projectValue2]"));
      assertThat(test.getStdOut(), containsString("key3=project: [projectValue3]"));
   }

   @Test
   public void testConfigClear() throws Exception
   {
      assertFalse(test.execute("config-set --key key1 --value userValue1", 15, TimeUnit.SECONDS) instanceof Failed);
      assertFalse(test.execute("config-list", 15, TimeUnit.SECONDS) instanceof Failed);
      assertThat(test.getStdOut(), containsString("key1=user: [userValue1]"));
      test.clearScreen();
      assertFalse(test.execute("config-clear --key key1", 15, TimeUnit.SECONDS) instanceof Failed);
      assertFalse(test.execute("config-list", 15, TimeUnit.SECONDS) instanceof Failed);
      assertThat(test.getStdOut(), not(containsString("key1=user: [userValue1]")));
   }

   @Test
   public void testConfigSetPropertyListInProject() throws Exception
   {
      Project project = projectFactory.createTempProject();
      test.getShell().setCurrentResource(project.getRoot());
      test.execute("config-set --key key2 --value projectValue2 --local", 15, TimeUnit.SECONDS);
      test.execute("config-set --key key3 --value projectValue3 --local", 15, TimeUnit.SECONDS);
      assertFalse(test.execute("config-list", 15, TimeUnit.SECONDS) instanceof Failed);
      assertThat(test.getStdOut(), containsString("key2=project: [projectValue2]"));
      assertThat(test.getStdOut(), containsString("key3=project: [projectValue3]"));
   }

   @Test
   public void testMergedConfigList() throws Exception
   {
      addPropsToUserConfig();
      Project project = projectFactory.createTempProject();
      Configuration projectConfig = project.getFacet(ConfigurationFacet.class).getConfiguration();
      addPropsToProjectConfig(projectConfig);
      test.getShell().setCurrentResource(project.getRoot());
      test.execute("config-list", 15, TimeUnit.SECONDS);
      assertThat(test.getStdOut(), containsString("key1=user: [userValue1]"));
      assertThat(test.getStdOut(), containsString("key2=user: [userValue2], project: [projectValue2]"));
      assertThat(test.getStdOut(), containsString("key3=project: [projectValue3]"));
   }

   @Test
   public void testSubSubsetConfigurationClearProperty() throws Exception
   {
      userConfig.clear();
      userConfig.setProperty("subset.subset.A", "Value");
      assertTrue(subSubsetConfiguration.getKeys().hasNext());
      subSubsetConfiguration.clearProperty("A");
      assertFalse(subSubsetConfiguration.getKeys().hasNext());
      assertFalse(userConfig.getKeys().hasNext());
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
      test.close();
   }
}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.env;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationImplTest extends AbstractShellTest
{
   private static String key = ConfigurationImplTest.class.getName() + "foo";

   @Inject
   private Configuration config;

   @Test
   public void testAccessUserConfigurationOutsideOfProject() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      String string = config.getString(key);
      Assert.assertNull(string);

      config.setProperty(key, "bar");
      config.getScopedConfiguration(ConfigurationScope.USER);
      Assert.assertNotNull(config);
   }

   @Test
   public void testAccessProjectConfigurationOutsideOfProject() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      String string = config.getString(key);
      Assert.assertNull(string);

      config.setProperty(key, "bar");
      try
      {
         config.getScopedConfiguration(ConfigurationScope.PROJECT);
         Assert.fail();
      }
      catch (Exception e)
      {
      }
   }

   @Inject
   private Event<InstallFacets> installFacets;

   @Test
   public void testAccessProjectConfigurationDuringProjectInitialization() throws Exception
   {
      config.clearProperty(MockConfigFacet.INSTALLED);
      Project project = initializeProject(PackagingType.JAR);
      installFacets.fire(new InstallFacets(MockConfigFacet.class));
      Assert.assertTrue(project.hasFacet(MockConfigFacet.class));
      config.clearProperty(MockConfigFacet.INSTALLED);
   }

   @Test
   public void testSettingDefaultConfigChoosesProjectOverUser() throws Exception
   {
      initializeProject(PackagingType.WAR);
      String string = config.getString(key);
      Assert.assertNull(string);

      config.setProperty(key, "bar");

      /*
       * By default, the write operations will persist to the first delegate (PROJECT), if no project is available they
       * will persist to the next delegate (user settings)
       */
      Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
      Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

      Assert.assertEquals("bar", config.getString(key));
      Assert.assertNull("bar", userConfig.getString(key));
      Assert.assertEquals("bar", projectConfig.getString(key));
   }

   @Test
   public void testSettingUserConfigDirectly() throws Exception
   {
      Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
      initializeProject(PackagingType.JAR);
      Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

      String string = userConfig.getString(key);
      Assert.assertNull(string);
      userConfig.setProperty(key, "bar");
      Assert.assertEquals("bar", userConfig.getString(key));

      Assert.assertNull(projectConfig.getString(key));
      Assert.assertEquals("bar", config.getString(key));
   }

   @Test
   public void testProjectConfigTakesReadPriority() throws Exception
   {
      initializeProject(PackagingType.JAR);
      Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
      Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

      String string = userConfig.getString(key);
      Assert.assertNull(string);
      userConfig.setProperty(key, "bar");
      projectConfig.setProperty(key, "bar2");

      Assert.assertEquals("bar", userConfig.getString(key));
      Assert.assertEquals("bar2", projectConfig.getString(key));
      Assert.assertEquals("bar2", config.getString(key));
   }

   @Test
   public void testClearProjectDoesNotClearGlobal() throws Exception
   {
      initializeProject(PackagingType.JAR);
      Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
      Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

      String string = userConfig.getString(key);
      Assert.assertNull(string);
      userConfig.setProperty(key, "bar");
      projectConfig.setProperty(key, "bar2");

      Assert.assertEquals("bar", userConfig.getString(key));
      Assert.assertEquals("bar2", projectConfig.getString(key));
      Assert.assertEquals("bar2", config.getString(key));

      projectConfig.clearProperty(key);

      Assert.assertEquals("bar", userConfig.getString(key));
      Assert.assertNull(projectConfig.getString(key));
      Assert.assertEquals("bar", config.getString(key));
   }

   @Test
   public void testClearUserDoesNotClearProject() throws Exception
   {
      initializeProject(PackagingType.JAR);
      Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
      Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

      String string = userConfig.getString(key);
      Assert.assertNull(string);
      userConfig.setProperty(key, "bar");
      projectConfig.setProperty(key, "bar2");

      Assert.assertEquals("bar", userConfig.getString(key));
      Assert.assertEquals("bar2", projectConfig.getString(key));
      Assert.assertEquals("bar2", config.getString(key));

      userConfig.clearProperty(key);

      Assert.assertNull(userConfig.getString(key));
      Assert.assertEquals("bar2", projectConfig.getString(key));
      Assert.assertEquals("bar2", config.getString(key));
   }

   @Test
   public void testClearAll() throws Exception
   {
      initializeProject(PackagingType.JAR);
      Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
      Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

      String string = userConfig.getString(key);
      Assert.assertNull(string);
      userConfig.setProperty(key, "bar");
      projectConfig.setProperty(key, "bar2");

      Assert.assertEquals("bar", userConfig.getString(key));
      Assert.assertEquals("bar2", projectConfig.getString(key));
      Assert.assertEquals("bar2", config.getString(key));

      config.clearProperty(key);

      Assert.assertNull(userConfig.getString(key));
      Assert.assertNull(projectConfig.getString(key));
      Assert.assertNull(config.getString(key));
   }

   @Test
   public void testListConfig() throws Exception
   {
      config.setProperty(key, "OMG!");
      getShell().execute("list-config");
   }

   @After
   public void afterTestConfig()
   {
      config.clearProperty(key);
      config.clearProperty(key);
   }
}

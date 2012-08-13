/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.exceptions.PluginExecutionException;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class SetupCommandTest extends AbstractShellTest
{
   @Inject
   private PluginRegistry registry;

   @Test
   public void testRegistryAllowsAccessToSetupCommand() throws Exception
   {
      initializeJavaProject();

      PluginMetadata pluginMetadata = registry.getPluginMetadataForScopeAndConstraints("testplugin", getShell());
      Assert.assertEquals(1, pluginMetadata.getAllCommands().size());
      Assert.assertTrue(pluginMetadata.hasSetupCommand());
      Assert.assertTrue(pluginMetadata.getSetupCommand().isSetup());
      Assert.assertEquals("setup", pluginMetadata.getSetupCommand().getName());
   }

   @Test
   public void testSetupCommandAccessible() throws Exception
   {
      initializeJavaProject();

      Assert.assertFalse(getProject().hasFacet(MockFacet.class));
      getShell().execute("setup testplugin");
      Assert.assertTrue(getProject().hasFacet(MockFacet.class));
   }

   @Test
   public void testSetupCommandAccessibleNative() throws Exception
   {
      initializeJavaProject();

      Assert.assertFalse(getProject().hasFacet(MockFacet.class));
      getShell().execute("testplugin setup");
      Assert.assertTrue(getProject().hasFacet(MockFacet.class));
   }

   @Test(expected = PluginExecutionException.class)
   public void testSetupCommandNotAccessibleWhenNoProject() throws Exception
   {
      DirectoryResource tempFolder = createTempFolder();
      getShell().setCurrentResource(tempFolder);

      getShell().execute("setup testplugin");
   }

   @Test(expected = PluginExecutionException.class)
   public void testSetupCommandNotAccessibleWhenNoProjectNative() throws Exception
   {
      DirectoryResource tempFolder = createTempFolder();
      getShell().setCurrentResource(tempFolder);

      getShell().execute("testplugin setup");
   }

   @Test
   public void testSetupCommandWithMultiplePlugins() throws Exception
   {
      initializeJavaProject();

      Assert.assertFalse(getProject().hasFacet(MockFacet.class));
      Assert.assertFalse(getProject().hasFacet(MockFacet2.class));

      getShell().execute("setup testplugin testplugin2");

      Assert.assertTrue(getProject().hasFacet(MockFacet.class));
      Assert.assertTrue(getProject().hasFacet(MockFacet2.class));
   }
}
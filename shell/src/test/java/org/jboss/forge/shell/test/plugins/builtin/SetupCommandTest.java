/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
   public void testSetupCommandWithMultiplePlugins() throws Exception {
	   initializeJavaProject();

	   Assert.assertFalse(getProject().hasFacet(MockFacet.class));
	   Assert.assertFalse(getProject().hasFacet(MockFacet2.class));
	   
	   getShell().execute("setup testplugin testplugin2");
	   
	   Assert.assertTrue(getProject().hasFacet(MockFacet.class));
	   Assert.assertTrue(getProject().hasFacet(MockFacet2.class));
   }
}
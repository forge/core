/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.InstalledPluginRegistry;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellImpl;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ForgePluginTest extends AbstractShellTest
{
   @Before
   public void beforePluginTest()
   {
      getShell().getEnvironment().setProperty(ShellImpl.PROP_DEFAULT_PLUGIN_REPO, ShellImpl.DEFAULT_PLUGIN_REPO);
   }

   @Test
   @Ignore
   public void testFindPlugin() throws Exception
   {
      Shell shell = getShell();
      shell.execute("forge find-plugin jsf");
   }

   @Test
   public void testLogo() throws Exception
   {
      getShell().execute("forge");
   }

   @Test
   @Ignore
   public void testGitPluginNoProject() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      getShell().execute("forge git-plugin git://github.com/forge/scaffold-aerogear.git");
   }

   @Test
   @Ignore
   public void testBuildPrettyfaces() throws Exception
   {
      getShell().getEnvironment().setProperty(ShellImpl.PROP_FORGE_VERSION, "1.0.3.Final");
      getShell().execute("forge install-plugin ocpsoft-prettyfaces");
   }

   @Test
   public void testListPlugins() throws Exception
   {
      InstalledPluginRegistry.install("test.test", "1.0.0-SNAPSHOT", "moo");
      getShell().execute("forge list-plugins arquillian");
      Assert.assertFalse(getOutput().contains("test.test"));

      getShell().execute("forge list-plugins test.test");
      Assert.assertTrue(getOutput().contains("test.test"));
   }

   @Test
   public void testListPluginsGrep() throws Exception
   {
      InstalledPluginRegistry.install("test.test", "1.0.0-SNAPSHOT", "moo");
      getShell().execute("forge list-plugins | grep arquillian");
      Assert.assertFalse(getOutput().contains("test.test"));

      getShell().execute("forge list-plugins | grep test.test");
      Assert.assertTrue(getOutput().contains("test.test"));
   }

}

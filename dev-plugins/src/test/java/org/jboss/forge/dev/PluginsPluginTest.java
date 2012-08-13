/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.dev;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.InstalledPluginRegistry;
import org.jboss.forge.shell.InstalledPluginRegistry.PluginEntry;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class PluginsPluginTest extends AbstractShellTest
{
   @Test
   public void testListFacets() throws Exception
   {
      initializeJavaProject();
      getShell().execute("project list-facets");
      String output = getOutput();
      assertTrue(output.contains("JavaSourceFacet"));
   }

   @Test
   public void testCreateAndBuildPlugin() throws Exception
   {
      initializeJavaProject();
      queueInputLines("", "14");
      getShell().execute("plugins setup");
      queueInputLines("");
      getShell().execute("plugins new-plugin --named demo");
      getShell().execute("build");
   }

   @Test
   public void testCreatePluginWithDashInName() throws Exception
   {
      initializeJavaProject();
      queueInputLines("", "14");
      getShell().execute("plugins setup");
      queueInputLines("");
      getShell().execute("plugins new-plugin --named demo-plugin");
      getShell().execute("build");
   }

   @Test
   public void testCreatePluginWithNumberInName() throws Exception
   {
      initializeJavaProject();
      queueInputLines("", "1");
      getShell().execute("plugins setup");
      queueInputLines("");
      getShell().execute("plugins new-plugin --named Wro4jPlugin");
      JavaResource resource = getProject().getFacet(JavaSourceFacet.class).getSourceFolder().getChildOfType(
               JavaResource.class, "com/test/Wro4jPlugin.java");
      assertTrue(resource.exists());
   }

   @Test
   public void testCreatePluginWithUppercaseName() throws Exception
   {
      initializeJavaProject();
      queueInputLines("", "14");
      getShell().execute("plugins setup");
      queueInputLines("");
      getShell().execute("plugins new-plugin --named DemoPlugin");
      getShell().execute("build");
   }

   @Test
   public void testInstallPlugin() throws Exception
   {
      Project javaProject = initializeJavaProject();
      queueInputLines("", "1");
      getShell().execute("plugins setup");
      queueInputLines("");
      getShell().execute("plugins new-plugin --named plugins-plugin-test-install");
      getShell().execute("cd ~~");
      getShell().execute("forge source-plugin .");

      PluginEntry installed = null;
      List<PluginEntry> installedPlugins = InstalledPluginRegistry.list();
      for (PluginEntry plugin : installedPlugins)
      {
         if (plugin.getName().contains(javaProject.getFacet(MetadataFacet.class).getProjectName()))
         {
            installed = plugin;
            getShell().execute("forge remove-plugin " + plugin);
         }
      }

      Assert.assertNotNull(installed);
      Assert.assertFalse(InstalledPluginRegistry.has(installed));

   }

}

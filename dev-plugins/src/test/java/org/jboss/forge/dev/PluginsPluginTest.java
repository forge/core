/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

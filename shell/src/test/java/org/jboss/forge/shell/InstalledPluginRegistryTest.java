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
package org.jboss.forge.shell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InstalledPluginRegistryTest
{
   /**
    * Test method for {@link org.jboss.forge.shell.InstalledPluginRegistry#getInstalledPlugins()}.
    */
   @Test
   public void testGetInstalledPlugins()
   {
      List<String> originals = InstalledPluginRegistry.getInstalledPlugins();
      InstalledPluginRegistry.installPlugin("test.test", "moo");
      List<String> plugins = InstalledPluginRegistry.getInstalledPlugins();

      boolean found = false;
      for (String plugin : plugins) {
         if ("test.test:moo".equals(plugin))
         {
            InstalledPluginRegistry.removePlugin(plugin);
            assertFalse(InstalledPluginRegistry.hasPlugin(plugin));
            found = true;
         }
      }
      assertTrue(found);

      assertSame(originals.size(), InstalledPluginRegistry.getInstalledPlugins().size());
      for (String plugin : originals) {
         assertTrue(InstalledPluginRegistry.hasPlugin(plugin));
      }
   }

   /**
    * Test method for {@link org.jboss.forge.shell.InstalledPluginRegistry#getInstalledPlugins()}.
    */
   @Test
   public void testAddNewVersion()
   {
      List<String> originals = InstalledPluginRegistry.getInstalledPlugins();
      InstalledPluginRegistry.installPlugin("test.test", "moo");
      InstalledPluginRegistry.installPlugin("test.test", "foo");
      List<String> plugins = InstalledPluginRegistry.getInstalledPlugins();

      boolean found = false;
      for (String plugin : plugins) {
         if (plugin.contains("test.test"))
         {
            InstalledPluginRegistry.removePlugin(plugin);
            assertFalse(InstalledPluginRegistry.hasPlugin(plugin));
            found = true;
         }
      }
      assertTrue(found);

      assertSame(originals.size(), InstalledPluginRegistry.getInstalledPlugins().size());
      for (String plugin : originals) {
         assertTrue(InstalledPluginRegistry.hasPlugin(plugin));
      }
   }

}

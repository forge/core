/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.forge.shell.InstalledPluginRegistry.PluginEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InstalledPluginRegistryTest
{
   private List<PluginEntry> installed;

   @Before
   public void before()
   {
      this.installed = InstalledPluginRegistry.list();
      for (PluginEntry p : installed)
      {
         InstalledPluginRegistry.remove(p);
      }
      Assert.assertTrue(InstalledPluginRegistry.list().isEmpty());
   }

   @After
   public void after()
   {
      List<PluginEntry> current = InstalledPluginRegistry.list();
      for (PluginEntry p : current)
      {
         InstalledPluginRegistry.remove(p);
      }
      for (PluginEntry p : installed)
      {
         InstalledPluginRegistry.install(p.getName(), p.getApiVersion(), p.getSlot());
      }
   }

   /**
    * Test method for {@link org.jboss.forge.shell.InstalledPluginRegistry#list()}.
    */
   @Test
   public void testGetInstalledPlugins()
   {
      List<PluginEntry> originals = InstalledPluginRegistry.list();
      assertFalse(InstalledPluginRegistry.has(new PluginEntry("test.test", "1.0.0-SNAPSHOT", "moo")));
      PluginEntry installed = InstalledPluginRegistry.install("test.test", "1.0.0-SNAPSHOT", "moo");
      assertTrue(InstalledPluginRegistry.has(installed));
      List<PluginEntry> plugins = InstalledPluginRegistry.list();

      boolean found = false;
      for (PluginEntry plugin : plugins)
      {
         if ("test.test:1.0.0-SNAPSHOT:moo".equals(plugin.toCoordinates()))
         {
            InstalledPluginRegistry.remove(plugin);
            assertFalse(InstalledPluginRegistry.has(plugin));
            found = true;
         }
      }
      assertTrue(found);

      assertSame(originals.size(), InstalledPluginRegistry.list().size());
      for (PluginEntry plugin : originals)
      {
         assertTrue(InstalledPluginRegistry.has(plugin));
         InstalledPluginRegistry.remove(plugin);
         assertFalse(InstalledPluginRegistry.has(plugin));
      }
   }

   /**
    * Test method for {@link org.jboss.forge.shell.InstalledPluginRegistry#list()}.
    */
   @Test
   public void testAddNewVersion()
   {
      List<PluginEntry> originals = InstalledPluginRegistry.list();
      InstalledPluginRegistry.install("test.test", "1.0.0-SNAPSHOT", "moo");
      InstalledPluginRegistry.install("test.test", "1.0.0-SNAPSHOT", "foo");
      List<PluginEntry> plugins = InstalledPluginRegistry.list();

      boolean found = false;
      for (PluginEntry plugin : plugins)
      {
         if (plugin.getName().equals("test.test"))
         {
            InstalledPluginRegistry.remove(plugin);
            assertFalse(InstalledPluginRegistry.has(plugin));
            found = true;
         }
      }
      assertTrue(found);

      assertSame(originals.size(), InstalledPluginRegistry.list().size());
      for (PluginEntry plugin : originals)
      {
         assertTrue(InstalledPluginRegistry.has(plugin));
         InstalledPluginRegistry.remove(plugin);
         assertFalse(InstalledPluginRegistry.has(plugin));
      }
   }

   @Test
   public void testMultipleVersions() throws Exception
   {
      PluginEntry one = InstalledPluginRegistry.install("foo", "1", "s1");
      PluginEntry two = InstalledPluginRegistry.install("foo", "2", "s2");

      assertFalse(InstalledPluginRegistry.has(one));
      assertTrue(InstalledPluginRegistry.has(two));

      InstalledPluginRegistry.remove(one);
      InstalledPluginRegistry.remove(two);

      assertFalse(InstalledPluginRegistry.has(one));
      assertFalse(InstalledPluginRegistry.has(two));
   }

   @Test(expected = IllegalStateException.class)
   public void testMinorVersionException() throws Exception
   {
      PluginEntry entry = PluginEntry.fromCoordinates("com.example.plugin:1.0.0-SNAPSHOT:main");
      Assert.assertFalse(InstalledPluginRegistry.isApiCompatible(null, entry));
   }

   @Test(expected = IllegalStateException.class)
   public void testMinorVersionException2() throws Exception
   {
      Assert.assertFalse(InstalledPluginRegistry.isApiCompatible("", (PluginEntry) null));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testMinorVersionException3() throws Exception
   {
      PluginEntry entry = PluginEntry.fromCoordinates("com.example.plugin::main");
      Assert.assertFalse(InstalledPluginRegistry.isApiCompatible("", entry));
   }

   @Test
   public void testMinorVersionCompatible() throws Exception
   {
      PluginEntry entry = PluginEntry.fromCoordinates("com.example.plugin:1.0.0-SNAPSHOT:main");
      Assert.assertTrue(InstalledPluginRegistry.isApiCompatible("1.0.1.Final", entry));
      Assert.assertTrue(InstalledPluginRegistry.isApiCompatible("1.0.2.Final", entry));
      Assert.assertTrue(InstalledPluginRegistry.isApiCompatible("1.0.2000.Final", entry));
      Assert.assertTrue(InstalledPluginRegistry.isApiCompatible("1.0.2-SNAPSHOT", entry));
      Assert.assertTrue(InstalledPluginRegistry.isApiCompatible("1.0.1000-SNAPSHOT", entry));
      Assert.assertTrue(InstalledPluginRegistry.isApiCompatible("1.0.1000-adsfasfsd", entry));
      Assert.assertFalse(InstalledPluginRegistry.isApiCompatible("1.1.0.Final", entry));
      Assert.assertFalse(InstalledPluginRegistry.isApiCompatible("2.0.0.Final", entry));
      Assert.assertFalse(InstalledPluginRegistry.isApiCompatible("s1.0.0.Final", entry));
      Assert.assertFalse(InstalledPluginRegistry.isApiCompatible("", entry));
   }

}

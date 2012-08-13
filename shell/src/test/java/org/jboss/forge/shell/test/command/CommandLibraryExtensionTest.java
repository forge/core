/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.forge.shell.command.CommandLibraryExtension;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.OptionMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.plugins.Plugin;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CommandLibraryExtensionTest
{
   CommandLibraryExtension library = new CommandLibraryExtension();
   private final PluginMetadata plugin = library.getMetadataFor(MockNamedPlugin.class);

   @Test
   public void testParsePluginWithoutHelp() throws Exception
   {
      assertEquals("mnp", plugin.getName());
      assertEquals("", plugin.getHelp());

      assertTrue(Plugin.class.isAssignableFrom(plugin.getType()));
   }

   @Test
   public void testParsePluginWithDefaultAndNormalCommands() throws Exception
   {
      assertEquals(6, plugin.getCommands().size());
      assertTrue(plugin.hasDefaultCommand());
      assertNotNull(plugin.getDefaultCommand());

      assertTrue(Plugin.class.isAssignableFrom(plugin.getType()));
   }

   @Test
   public void testParseDefaultCommand() throws Exception
   {
      CommandMetadata defaultCommand = plugin.getDefaultCommand();

      assertEquals(plugin.getName(), defaultCommand.getName());
      assertEquals("This is a mock default command", defaultCommand.getHelp());
   }

   @Test
   public void testParseNormalCommand() throws Exception
   {
      CommandMetadata normal = plugin.getCommand("normal");

      assertEquals("normal", normal.getName());
      assertEquals("", normal.getHelp());
   }

   @Test
   public void testParseHelplessCommand() throws Exception
   {
      CommandMetadata normal = plugin.getCommand("helpless");
      assertEquals("helpless", normal.getName());
      assertEquals("", normal.getHelp());
   }

   @Test
   public void testParseOptions() throws Exception
   {
      CommandMetadata command = plugin.getCommand("normal");

      List<OptionMetadata> options = command.getOptions();

      assertEquals(1, options.size());
      assertEquals("", options.get(0).getName());
      assertEquals("THE OPTION", options.get(0).getDescription());
   }

   @Test
   public void testParseNamedOption() throws Exception
   {
      CommandMetadata command = plugin.getCommand("named");

      List<OptionMetadata> options = command.getOptions();
      OptionMetadata option = options.get(0);

      assertEquals(1, options.size());
      assertEquals("named", option.getName());
      assertEquals("", option.getDescription());
      assertEquals("true", option.getDefaultValue());
   }
}

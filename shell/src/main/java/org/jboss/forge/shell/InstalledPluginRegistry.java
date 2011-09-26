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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.parser.xml.XMLParserException;
import org.jboss.forge.shell.util.OSUtils;
import org.jboss.forge.shell.util.Streams;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class InstalledPluginRegistry
{
   private static final String REGISTRY = "/.forge/plugins/installed.xml";

   public static List<String> getInstalledPlugins()
   {
      List<String> result = new ArrayList<String>();
      File registryFile = new File(OSUtils.getUserHomePath() + REGISTRY);
      try {
         Node installed = XMLParser.parse(new FileInputStream(registryFile));
         List<Node> list = installed.get("plugin");
         for (Node plugin : list) {
            result.add(plugin.getAttribute("name") + ":" + plugin.getAttribute("slot"));
         }
      }
      catch (XMLParserException e) {
         throw new RuntimeException("Invalid syntax in [" + registryFile.getAbsolutePath()
                  + "] - Please delete this file and restart Forge", e);
      }
      catch (FileNotFoundException e) {
         // this is OK, no plugins installed
      }
      return result;
   }

   public static void installPlugin(String plugin, String slot)
   {
      if (Strings.isNullOrEmpty(plugin))
      {
         throw new RuntimeException("Plugin must not be null");
      }
      if (Strings.isNullOrEmpty(slot))
      {
         slot = "main";
      }

      Node installed = null;
      File registryFile = new File(OSUtils.getUserHomePath() + REGISTRY);
      try {

         if (registryFile.exists())
         {
            installed = XMLParser.parse(new FileInputStream(registryFile));
         }
         else
         {
            registryFile.mkdirs();
            registryFile.delete();
            registryFile.createNewFile();

            installed = XMLParser.parse("<installed></installed>");
         }

         installed.getOrCreate("plugin@name=" + plugin).attribute("slot", slot);
         Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));
      }
      catch (FileNotFoundException e) {
         throw new RuntimeException("Could not read [" + registryFile.getAbsolutePath()
                  + "] - ", e);
      }
      catch (IOException e) {
         throw new RuntimeException("Error manipulating [" + registryFile.getAbsolutePath()
                  + "] - ", e);
      }
   }

   public static void removePlugin(String plugin)
   {
      if (Strings.isNullOrEmpty(plugin))
      {
         throw new RuntimeException("Plugin must not be null");
      }

      if (plugin.contains(":"))
      {
         plugin = plugin.split(":")[0];
      }

      File registryFile = new File(OSUtils.getUserHomePath() + REGISTRY);
      if (registryFile.exists())
      {
         try {
            Node installed = XMLParser.parse(new FileInputStream(registryFile));

            Node child = installed.getSingle("plugin@name=" + plugin);
            installed.removeChild(child);
            Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));
         }
         catch (FileNotFoundException e) {
            // already removed
         }
      }
   }

   public static boolean hasPlugin(String plugin)
   {
      if (Strings.isNullOrEmpty(plugin))
      {
         throw new RuntimeException("Plugin must not be null");
      }

      if (plugin.contains(":"))
      {
         plugin = plugin.split(":")[0];
      }

      File registryFile = new File(OSUtils.getUserHomePath() + REGISTRY);
      if (registryFile.exists())
      {
         try {
            Node installed = XMLParser.parse(new FileInputStream(registryFile));

            Node child = installed.getSingle("plugin@name=" + plugin);
            if (child != null)
            {
               return true;
            }
         }
         catch (FileNotFoundException e) {
            // already removed
         }
      }

      return false;
   }

}

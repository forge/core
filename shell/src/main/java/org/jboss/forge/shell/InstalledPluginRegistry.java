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
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.parser.xml.XMLParserException;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.util.OSUtils;
import org.jboss.forge.shell.util.Streams;

/**
 * Used to perform {@link Plugin} installation/registration operations.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class InstalledPluginRegistry
{
   private static final String DEFAULT_SLOT = "main";
   private static final String ATTR_SLOT = "slot";
   private static final String ATTR_API_VERSION = "api-version";
   private static final String ATTR_NAME = "name";
   private static final String REGISTRY = "/.forge/plugins/installed.xml";

   public static File getRegistryFile()
   {
      return new File(REGISTRY);
   }

   public static List<PluginEntry> listByVersion(final String version)
   {
      List<PluginEntry> list = list();
      List<PluginEntry> result = list;

      if (version != null)
      {
         result = new ArrayList<InstalledPluginRegistry.PluginEntry>();
         for (PluginEntry entry : list) {
            if (version.equals(entry.getApiVersion()))
            {
               result.add(entry);
            }
         }
      }

      return result;
   }

   public static List<PluginEntry> list()
   {
      List<PluginEntry> result = new ArrayList<PluginEntry>();
      File registryFile = new File(OSUtils.getUserHomePath() + REGISTRY);
      try {
         Node installed = XMLParser.parse(new FileInputStream(registryFile));
         List<Node> list = installed.get("plugin");
         for (Node plugin : list) {
            PluginEntry entry = new PluginEntry(plugin.getAttribute(ATTR_NAME), plugin.getAttribute(ATTR_API_VERSION),
                     plugin.getAttribute(ATTR_SLOT));
            result.add(entry);
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

   public static PluginEntry install(final String name, final String apiVersion, String slot)
   {
      if (Strings.isNullOrEmpty(name))
      {
         throw new RuntimeException("Plugin must not be null");
      }
      if (Strings.isNullOrEmpty(apiVersion))
      {
         throw new RuntimeException("API version must not be null");
      }
      if (Strings.isNullOrEmpty(slot))
      {
         slot = DEFAULT_SLOT;
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

         installed.getOrCreate("plugin@" + ATTR_NAME + "=" + name + "&" + ATTR_API_VERSION + "=" + apiVersion)
                  .attribute(ATTR_SLOT, slot);
         Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));

         return new PluginEntry(name, apiVersion, slot);
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

   public static void remove(final PluginEntry plugin)
   {
      if (plugin == null)
      {
         throw new RuntimeException("Plugin must not be null");
      }

      File registryFile = new File(OSUtils.getUserHomePath() + REGISTRY);
      if (registryFile.exists())
      {
         try {
            Node installed = XMLParser.parse(new FileInputStream(registryFile));

            Node child = installed.getSingle("plugin@" + ATTR_NAME + "=" + plugin.getName() + "&" + ATTR_API_VERSION
                     + "=" + plugin.getApiVersion());
            installed.removeChild(child);
            Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));
         }
         catch (FileNotFoundException e) {
            // already removed
         }
      }
   }

   public static PluginEntry get(final PluginEntry plugin)
   {
      if (plugin == null)
      {
         throw new RuntimeException("Plugin must not be null");
      }

      File registryFile = new File(OSUtils.getUserHomePath() + REGISTRY);
      if (registryFile.exists())
      {
         try {
            Node installed = XMLParser.parse(new FileInputStream(registryFile));

            Node child = installed.getSingle("plugin@" + ATTR_NAME + "=" + plugin.getName());
            if (child != null)
            {
               if ((plugin.getApiVersion() == null)
                        || plugin.getApiVersion().equals(child.getAttribute(ATTR_API_VERSION)))
               {
                  if ((plugin.getSlot() == null)
                           || plugin.getSlot().equals(child.getAttribute(ATTR_SLOT)))
                  {
                     return new PluginEntry(child.getAttribute(ATTR_NAME), child.getAttribute(ATTR_API_VERSION),
                              child.getAttribute(ATTR_SLOT));
                  }
               }
            }
         }
         catch (FileNotFoundException e) {
            // already removed
         }
      }

      return null;
   }

   public static boolean has(final PluginEntry plugin)
   {
      return get(plugin) != null;
   }

   public static class PluginEntry
   {
      private final String name;
      private final String apiVersion;
      private final String slot;

      public PluginEntry(final String name, final String apiVersion, final String slot)
      {
         this.name = name;
         this.apiVersion = apiVersion;
         this.slot = slot;
      }

      public PluginEntry(final String name, final String apiVersion)
      {
         this.name = name;
         this.apiVersion = apiVersion;
         this.slot = null;
      }

      public PluginEntry(final String name)
      {
         this.name = name;
         this.apiVersion = null;
         this.slot = null;
      }

      public String getName()
      {
         return name;
      }

      public String getApiVersion()
      {
         return apiVersion;
      }

      public String getSlot()
      {
         return slot;
      }

      @Override
      public String toString()
      {
         return name + ":" + apiVersion + ":" + slot;
      }

      public static PluginEntry fromCoordinates(final String coordinates)
      {
         String[] split = coordinates.split(":");
         List<String> tokens = Arrays.asList(split);

         if (tokens.size() == 1)
         {
            return new PluginEntry(tokens.get(0));
         }
         else if (tokens.size() == 2)
         {
            return new PluginEntry(tokens.get(0), tokens.get(1));
         }
         else if (tokens.size() == 3)
         {
            return new PluginEntry(tokens.get(0), tokens.get(1), tokens.get(2));
         }
         else
         {
            throw new IllegalArgumentException("Coordinates must be of the form 'name:apiVersion:slot'");
         }

      }

      public String toModuleId()
      {
         return name + ":" + slot;
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = (prime * result) + ((apiVersion == null) ? 0 : apiVersion.hashCode());
         result = (prime * result) + ((name == null) ? 0 : name.hashCode());
         result = (prime * result) + ((slot == null) ? 0 : slot.hashCode());
         return result;
      }

      @Override
      public boolean equals(final Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         PluginEntry other = (PluginEntry) obj;
         if (apiVersion == null) {
            if (other.apiVersion != null)
               return false;
         }
         else if (!apiVersion.equals(other.apiVersion))
            return false;
         if (name == null) {
            if (other.name != null)
               return false;
         }
         else if (!name.equals(other.name))
            return false;
         if (slot == null) {
            if (other.slot != null)
               return false;
         }
         else if (!slot.equals(other.slot))
            return false;
         return true;
      }

      public String toCoordinates()
      {
         return toString();
      }

   }
}

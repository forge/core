/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.forge.parser.java.util.Assert;
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
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
public class InstalledPluginRegistry
{
   private static final String DEFAULT_SLOT = "main";
   private static final String ATTR_SLOT = "slot";
   private static final String ATTR_API_VERSION = "api-version";
   private static final String ATTR_NAME = "name";
   private static final String PLUGIN_DIR_DEFAULT = "/.forge/plugins";
   private static final String REGISTRY_FILE = "/installed.xml";

   private static String PLUGIN_DIR = null;
   private static String REGISTRY = null;

   private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(\\.|-)(.*)");

   private static String getPluginDir()
   {
      if (PLUGIN_DIR == null)
      {
         PLUGIN_DIR = System.getProperty(Bootstrap.PROP_PLUGIN_DIR);
         if (PLUGIN_DIR == null)
         {
            PLUGIN_DIR = OSUtils.getUserHomePath() + PLUGIN_DIR_DEFAULT;
         }
      }
      return PLUGIN_DIR;
   }

   private static String getRegistry()
   {
      if (REGISTRY == null)
      {
         REGISTRY = getPluginDir() + REGISTRY_FILE;
      }
      return REGISTRY;
   }

   public static File getRegistryFile()
   {
      return new File(getRegistry());
   }

   public static List<PluginEntry> listByAPICompatibleVersion(final String version)
   {
      List<PluginEntry> list = list();
      List<PluginEntry> result = list;

      if (version != null)
      {
         result = new ArrayList<PluginEntry>();
         for (PluginEntry entry : list)
         {
            if (isApiCompatible(version, entry))
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
      File registryFile = getRegistryFile();
      try
      {
         Node installed = XMLParser.parse(new FileInputStream(registryFile));
         List<Node> list = installed.get("plugin");
         for (Node plugin : list)
         {
            PluginEntry entry = new PluginEntry(plugin.getAttribute(ATTR_NAME),
                     plugin.getAttribute(ATTR_API_VERSION),
                     plugin.getAttribute(ATTR_SLOT));
            result.add(entry);
         }
      }
      catch (XMLParserException e)
      {
         throw new RuntimeException("Invalid syntax in [" + registryFile.getAbsolutePath()
                  + "] - Please delete this file and restart Forge", e);
      }
      catch (FileNotFoundException e)
      {
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

      List<PluginEntry> installedPlugins = list();
      for (PluginEntry e : installedPlugins)
      {
         if (name.equals(e.getName()))
         {
            remove(e);
         }
      }

      Node installed = null;
      File registryFile = getRegistryFile();
      try
      {

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
      catch (FileNotFoundException e)
      {
         throw new RuntimeException("Could not read [" + registryFile.getAbsolutePath()
                  + "] - ", e);
      }
      catch (IOException e)
      {
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

      File registryFile = getRegistryFile();
      if (registryFile.exists())
      {
         try
         {
            Node installed = XMLParser.parse(new FileInputStream(registryFile));

            Node child = installed.getSingle("plugin@" + ATTR_NAME + "=" + plugin.getName() + "&"
                     + ATTR_API_VERSION
                     + "=" + plugin.getApiVersion());
            installed.removeChild(child);
            Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));
         }
         catch (FileNotFoundException e)
         {
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

      File registryFile = getRegistryFile();
      if (registryFile.exists())
      {
         try
         {
            Node installed = XMLParser.parse(new FileInputStream(registryFile));

            List<Node> children = installed.get("plugin@" + ATTR_NAME + "=" + plugin.getName());
            for (Node child : children)
            {
               if (child != null)
               {
                  if ((plugin.getApiVersion() == null)
                           || plugin.getApiVersion().equals(child.getAttribute(ATTR_API_VERSION)))
                  {
                     if ((plugin.getSlot() == null)
                              || plugin.getSlot().equals(child.getAttribute(ATTR_SLOT)))
                     {
                        return new PluginEntry(child.getAttribute(ATTR_NAME),
                                 child.getAttribute(ATTR_API_VERSION),
                                 child.getAttribute(ATTR_SLOT));
                     }
                  }
               }
            }
         }
         catch (FileNotFoundException e)
         {
            // already removed
         }
      }

      return null;
   }

   public static boolean has(final PluginEntry plugin)
   {
      return get(plugin) != null;
   }

   public static String getRuntimeAPIVersion()
   {
      return InstalledPluginRegistry.class.getPackage()
               .getImplementationVersion();
   }

   public static boolean isApiCompatible(CharSequence runtimeVersion, PluginEntry entry)
   {
      Assert.notNull(runtimeVersion, "Runtime API version must not be null.");
      Assert.notNull(entry, "Plugin entry must not be null.");
      String pluginApiVersion = entry.getApiVersion();
      Assert.notNull(pluginApiVersion, "Plugin entry.getApiVersion() must not be null.");

      return isApiCompatible(runtimeVersion, pluginApiVersion);
   }

   /**
    * This method only returns true if:
    *
    * - The major version of pluginApiVersion is equal to the major version of runtimeVersion AND
    *
    * - The minor version of pluginApiVersion is less or equal to the minor version of runtimeVersion
    *
    * @param runtimeVersion a version in the format x.x.x
    * @param pluginApiVersion a version in the format x.x.x
    * @return
    */
   public static boolean isApiCompatible(CharSequence runtimeVersion, String pluginApiVersion)
   {
      Matcher runtimeMatcher = VERSION_PATTERN.matcher(runtimeVersion);
      if (runtimeMatcher.matches())
      {
         int runtimeMajorVersion = Integer.parseInt(runtimeMatcher.group(1));
         int runtimeMinorVersion = Integer.parseInt(runtimeMatcher.group(2));

         Matcher pluginApiMatcher = VERSION_PATTERN.matcher(pluginApiVersion);
         if (pluginApiMatcher.matches())
         {
            int pluginApiMajorVersion = Integer.parseInt(pluginApiMatcher.group(1));
            int pluginApiMinorVersion = Integer.parseInt(pluginApiMatcher.group(2));

            if (pluginApiMajorVersion == runtimeMajorVersion && pluginApiMinorVersion <= runtimeMinorVersion)
            {
               return true;
            }
         }
      }
      return false;
   }
}

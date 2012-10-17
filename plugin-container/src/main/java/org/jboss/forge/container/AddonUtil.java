/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.OSUtils;
import org.jboss.forge.container.util.Streams;
import org.jboss.forge.container.util.Strings;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.parser.xml.XMLParserException;

/**
 * Used to perform Addon installation/registration operations.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
public final class AddonUtil
{

   public static final String PROP_ADDON_DIR = "org.jboss.forge.addonDir";
   private static final String DEFAULT_SLOT = "main";
   private static final String ATTR_SLOT = "slot";
   private static final String ATTR_API_VERSION = "api-version";
   private static final String ATTR_NAME = "name";
   private static final String ADDON_DIR_DEFAULT = "/.forge/addons";
   private static final String REGISTRY_FILE = "/installed.xml";

   private static String ADDON_DIR = null;
   private static String REGISTRY = null;

   public static String getAddonDirName()
   {
      if (ADDON_DIR == null)
      {
         ADDON_DIR = System.getProperty(PROP_ADDON_DIR);
         if (ADDON_DIR == null)
         {
            ADDON_DIR = OSUtils.getUserHomePath() + ADDON_DIR_DEFAULT;
         }
      }
      return ADDON_DIR;
   }

   public static String getRegistryFileName()
   {
      if (REGISTRY == null)
      {
         REGISTRY = getAddonDirName() + REGISTRY_FILE;
      }
      return REGISTRY;
   }

   public static File getRegistryFile()
   {
      try
      {
         File registryFile = new File(getRegistryFileName());
         if (!registryFile.exists())
         {
            registryFile.mkdirs();
            registryFile.delete();
            registryFile.createNewFile();

            FileOutputStream stream = new FileOutputStream(registryFile);
            Streams.write(XMLParser.toXMLInputStream(XMLParser.parse("<installed></installed>")), stream);
            stream.close();
         }
         return registryFile;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error initializing addon registry file.", e);
      }
   }

   public static List<AddonEntry> listByAPICompatibleVersion(final String version)
   {
      List<AddonEntry> list = list();
      List<AddonEntry> result = list;

      if (version != null)
      {
         result = new ArrayList<AddonUtil.AddonEntry>();
         for (AddonEntry entry : list)
         {
            if (isApiCompatible(version, entry))
            {
               result.add(entry);
            }
         }
      }

      return result;
   }

   public static List<AddonEntry> list()
   {
      List<AddonEntry> result = new ArrayList<AddonEntry>();
      File registryFile = getRegistryFile();
      try
      {
         Node installed = XMLParser.parse(new FileInputStream(registryFile));
         List<Node> list = installed.get("addon");
         for (Node addon : list)
         {
            AddonEntry entry = new AddonEntry(addon.getAttribute(ATTR_NAME),
                     addon.getAttribute(ATTR_API_VERSION),
                     addon.getAttribute(ATTR_SLOT));
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
         // this is OK, no addons installed
      }
      return result;
   }

   public static AddonEntry install(AddonEntry addon)
   {
      return install(addon.getName(), addon.getApiVersion(), addon.getSlot());
   }

   public static AddonEntry install(final String name, final String apiVersion, String slot)
   {
      if (Strings.isNullOrEmpty(name))
      {
         throw new RuntimeException("Addon must not be null");
      }
      if (Strings.isNullOrEmpty(apiVersion))
      {
         throw new RuntimeException("API version must not be null");
      }
      if (Strings.isNullOrEmpty(slot))
      {
         slot = DEFAULT_SLOT;
      }

      List<AddonEntry> installedAddons = list();
      for (AddonEntry e : installedAddons)
      {
         if (name.equals(e.getName()))
         {
            remove(e);
         }
      }

      File registryFile = getRegistryFile();
      try
      {
         Node installed = XMLParser.parse(new FileInputStream(registryFile));

         installed.getOrCreate("addon@" + ATTR_NAME + "=" + name + "&" + ATTR_API_VERSION + "=" + apiVersion)
                  .attribute(ATTR_SLOT, slot);
         Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));

         return new AddonEntry(name, apiVersion, slot);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException("Could not read [" + registryFile.getAbsolutePath()
                  + "] - ", e);
      }
   }

   public static void remove(final AddonEntry addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("Addon must not be null");
      }

      File registryFile = getRegistryFile();
      if (registryFile.exists())
      {
         try
         {
            Node installed = XMLParser.parse(new FileInputStream(registryFile));

            Node child = installed.getSingle("addon@" + ATTR_NAME + "=" + addon.getName() + "&"
                     + ATTR_API_VERSION
                     + "=" + addon.getApiVersion());
            installed.removeChild(child);
            Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));
         }
         catch (FileNotFoundException e)
         {
            // already removed
         }
      }
   }

   public static AddonEntry get(final AddonEntry addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("Addon must not be null");
      }

      File registryFile = getRegistryFile();
      try
      {
         Node installed = XMLParser.parse(new FileInputStream(registryFile));

         List<Node> children = installed.get("addon@" + ATTR_NAME + "=" + addon.getName());
         for (Node child : children)
         {
            if (child != null)
            {
               if ((addon.getApiVersion() == null)
                        || addon.getApiVersion().equals(child.getAttribute(ATTR_API_VERSION)))
               {
                  if ((addon.getSlot() == null)
                           || addon.getSlot().equals(child.getAttribute(ATTR_SLOT)))
                  {
                     return new AddonEntry(child.getAttribute(ATTR_NAME),
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

      return null;
   }

   public static File getAddonResourceDir(AddonEntry found)
   {
      Assert.notNull(found.getSlot(), "Addon slot must be specified.");
      Assert.notNull(found.getName(), "Addon name must be specified.");

      String path = found.getName().replaceAll("\\.", "/");
      File addonDir = new File(getAddonDirName() + "/" + path + "/" + found.getSlot());
      return addonDir;
   }

   public static File getAddonBaseDir(AddonEntry found)
   {
      Assert.notNull(found.getSlot(), "Addon slot must be specified.");
      Assert.notNull(found.getName(), "Addon name must be specified.");

      String path = found.getName().split("\\.")[0];
      File addonDir = new File(getAddonDirName() + "/" + path);
      return addonDir;
   }

   public static boolean has(final AddonEntry addon)
   {
      return get(addon) != null;
   }

   public static class AddonEntry
   {
      private final String name;
      private final String apiVersion;
      private final String slot;

      public AddonEntry(final String name, final String apiVersion, final String slot)
      {
         this.name = name;
         this.apiVersion = apiVersion;
         this.slot = slot;
      }

      public AddonEntry(final String name, final String apiVersion)
      {
         this.name = name;
         this.apiVersion = apiVersion;
         this.slot = null;
      }

      public AddonEntry(final String name)
      {
         this.name = name;
         this.apiVersion = null;
         this.slot = null;
      }

      public AddonEntry(AddonEntry entry)
      {
         this.name = entry.getName();
         this.apiVersion = entry.getApiVersion();
         this.slot = entry.getSlot();
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

      public static AddonEntry fromCoordinates(final String coordinates)
      {
         String[] split = coordinates.split(":");
         List<String> tokens = Arrays.asList(split);

         if (tokens.size() == 3)
         {
            if (Strings.isNullOrEmpty(tokens.get(0)))
               throw new IllegalArgumentException("Name was empty [" + coordinates + "]");
            if (Strings.isNullOrEmpty(tokens.get(1)))
               throw new IllegalArgumentException("Version was empty [" + coordinates + "]");
            if (Strings.isNullOrEmpty(tokens.get(2)))
               throw new IllegalArgumentException("Slot was empty [" + coordinates + "]");

            return new AddonEntry(tokens.get(0), tokens.get(1), tokens.get(2));
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
         AddonEntry other = (AddonEntry) obj;
         if (apiVersion == null)
         {
            if (other.apiVersion != null)
               return false;
         }
         else if (!apiVersion.equals(other.apiVersion))
            return false;
         if (name == null)
         {
            if (other.name != null)
               return false;
         }
         else if (!name.equals(other.name))
            return false;
         if (slot == null)
         {
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

   public static String getRuntimeAPIVersion()
   {
      String version = AddonUtil.class.getPackage()
               .getImplementationVersion();
      return version;
   }

   public static boolean isApiCompatible(CharSequence runtimeVersion, AddonEntry entry)
   {
      Assert.notNull(runtimeVersion, "Runtime API version must not be null.");
      Assert.notNull(entry, "Addon entry must not be null.");
      String addonApiVersion = entry.getApiVersion();
      Assert.notNull(addonApiVersion, "Addon entry.getApiVersion() must not be null.");

      return isApiCompatible(runtimeVersion, addonApiVersion);
   }

   public static boolean isApiCompatible(CharSequence runtimeVersion, String addonApiVersion)
   {
      Pattern runtimeVersionPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(\\.|-)(.*)");
      Matcher matcher = runtimeVersionPattern.matcher(runtimeVersion);
      if (matcher.matches())
      {
         if (addonApiVersion.matches(matcher.group(1) + "\\." + matcher.group(2) + "\\.(\\d+).*"))
         {
            return true;
         }
      }

      return false;
   }

   public static List<File> getAddonResources(AddonEntry found)
   {
      File dir = AddonUtil.getAddonResourceDir(found);
      if (dir.exists())
      {
         return Arrays.asList(dir.listFiles(new FilenameFilter()
         {
            @Override
            public boolean accept(File file, String name)
            {
               return name.endsWith(".jar");
            }
         }));
      }
      return new ArrayList<File>();
   }

   public static File getAddonSlotDir(AddonEntry addon)
   {
      return new File(getAddonBaseDir(addon).getAbsolutePath() + "/" + addon.getSlot());
   }

   public static List<AddonDependency> getAddonDependencies(AddonEntry addon)
   {
      List<AddonDependency> result = new ArrayList<AddonUtil.AddonDependency>();
      File descriptor = getAddonDescriptor(addon);

      try
      {
         Node installed = XMLParser.parse(new FileInputStream(descriptor));

         List<Node> children = installed.get("dependency");
         for (Node child : children)
         {
            if (child != null)
            {
               result.add(new AddonDependency(
                        child.getAttribute(ATTR_NAME),
                        child.getAttribute("min-version"),
                        child.getAttribute("max-version"),
                        Boolean.valueOf(child.getAttribute("optional"))));
            }
         }
      }
      catch (FileNotFoundException e)
      {
         // already removed
      }

      return result;
   }

   public static File getAddonDescriptor(AddonEntry addon)
   {
      File descriptorFile = new File(getAddonResourceDir(addon).getAbsolutePath() + "/forge.xml");
      try
      {
         if (!descriptorFile.exists())
         {
            descriptorFile.mkdirs();
            descriptorFile.delete();
            descriptorFile.createNewFile();

            FileOutputStream stream = new FileOutputStream(descriptorFile);
            Streams.write(XMLParser.toXMLInputStream(XMLParser.parse("<addon/>")), stream);
            stream.close();
         }
         return descriptorFile;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error initializing addon descriptor file.", e);
      }
   }

   public static class AddonDependency
   {
      private String name;
      private String minVersion = null;
      private String maxVersion = null;

      private boolean optional = false;

      public AddonDependency(String name, String minVersion, String maxVersion)
      {
         this(name, minVersion, maxVersion, false);
      }

      public AddonDependency(String name, String minVersion, String maxVersion, boolean optional)
      {
         this.optional = optional;
         this.name = name;
         this.minVersion = minVersion;
         this.maxVersion = maxVersion;
      }

      public String getName()
      {
         return name;
      }

      public String getMinVersion()
      {
         return minVersion;
      }

      public String getMaxVersion()
      {
         return maxVersion;
      }

      public boolean isOptional()
      {
         return optional;
      }

      @Override
      public String toString()
      {
         return "AddonDependency [name=" + name + ", minVersion=" + minVersion + ", maxVersion=" + maxVersion
                  + ", optional=" + optional + "]";
      }

      @Override
      public int hashCode()
      {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj)
      {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         AddonDependency other = (AddonDependency) obj;
         if (name == null)
         {
            if (other.name != null)
               return false;
         }
         else if (!name.equals(other.name))
            return false;
         return true;
      }

   }

   public static boolean hasRuntimeAPIVersion()
   {
      return getRuntimeAPIVersion() != null;
   }
}

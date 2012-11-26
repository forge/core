/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.Typed;

import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonEntry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.container.exception.AddonDeploymentException;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.Files;
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
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@Typed()
public final class AddonRepositoryImpl implements AddonRepository
{
   public static String getRuntimeAPIVersion()
   {
      String version = AddonRepository.class.getPackage()
               .getImplementationVersion();
      return version;
   }

   public static boolean hasRuntimeAPIVersion()
   {
      return getRuntimeAPIVersion() != null;
   }

   public static boolean isApiCompatible(CharSequence runtimeVersion, AddonEntry entry)
   {
      Assert.notNull(runtimeVersion, "Runtime API version must not be null.");
      Assert.notNull(entry, "Addon entry must not be null.");
      String addonApiVersion = entry.getApiVersion();
      Assert.notNull(addonApiVersion, "Addon entry.getApiVersion() must not be null.");

      return isApiCompatible(runtimeVersion, addonApiVersion);
   }

   /**
    * This method only returns true if:
    * 
    * - The major version of pluginApiVersion is equal to the major version of runtimeVersion AND
    * 
    * - The minor version of pluginApiVersion is less or equal to the minor version of runtimeVersion
    * 
    * @param runtimeVersion a version in the format x.x.x
    * @param addonApiVersion a version in the format x.x.x
    * @return
    */
   public static boolean isApiCompatible(CharSequence runtimeVersion, CharSequence addonApiVersion)
   {
      Matcher runtimeMatcher = VERSION_PATTERN.matcher(runtimeVersion);
      if (runtimeMatcher.matches())
      {
         int runtimeMajorVersion = Integer.parseInt(runtimeMatcher.group(1));
         int runtimeMinorVersion = Integer.parseInt(runtimeMatcher.group(2));

         Matcher addonApiMatcher = VERSION_PATTERN.matcher(addonApiVersion);
         if (addonApiMatcher.matches())
         {
            int addonApiMajorVersion = Integer.parseInt(addonApiMatcher.group(1));
            int addonApiMinorVersion = Integer.parseInt(addonApiMatcher.group(2));

            if (addonApiMajorVersion == runtimeMajorVersion && addonApiMinorVersion <= runtimeMinorVersion)
            {
               return true;
            }
         }
      }
      return false;
   }

   private static final String ATTR_SLOT = "slot";
   private static final String ATTR_API_VERSION = "api-version";
   private static final String ATTR_NAME = "name";
   private static final String ADDON_DIR_DEFAULT = ".forge/addons";
   private static final String REGISTRY_FILE_NAME = "installed.xml";
   private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(\\.|-)(.*)");
   private static final String FAR_DESCRIPTOR_FILENAME = "forge.xml";

   private File addonDir;

   private AddonRepositoryImpl(File dir)
   {
      Assert.notNull(dir, "Addon directory must not be null");
      this.addonDir = dir;
   }

   public static AddonRepository forAddonDir(File dir)
   {
      return new AddonRepositoryImpl(dir);
   }

   public static AddonRepository forDefaultAddonDir()
   {
      return new AddonRepositoryImpl(new File(OSUtils.getUserHomePath() + ADDON_DIR_DEFAULT));
   }

   public File getRepositoryDirectory()
   {
      if (!addonDir.exists() || !addonDir.isDirectory())
      {
         addonDir.delete();
         System.gc();
         if (!addonDir.mkdirs())
         {
            throw new RuntimeException("Could not create Addon Directory [" + addonDir + "]");
         }
      }
      return addonDir;
   }

   public synchronized File getRegistryFile()
   {
      File registryFile = new File(getRepositoryDirectory(), REGISTRY_FILE_NAME);
      try
      {
         if (!registryFile.exists())
         {
            registryFile.createNewFile();

            FileOutputStream out = new FileOutputStream(registryFile);
            try
            {
               Streams.write(XMLParser.toXMLInputStream(XMLParser.parse("<installed></installed>")), out);
            }
            finally
            {
               out.close();
            }
         }
         return registryFile;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error initializing addon registry file [" + registryFile + "]", e);
      }
   }

   public synchronized List<AddonEntry> listByAPICompatibleVersion(final String version)
   {
      List<AddonEntry> list = listInstalled();
      List<AddonEntry> result = list;

      if (version != null)
      {
         result = new ArrayList<AddonEntry>();
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

   public synchronized List<AddonEntry> listInstalled()
   {
      List<AddonEntry> result = new ArrayList<AddonEntry>();
      File registryFile = getRegistryFile();
      try
      {
         Node installed = XMLParser.parse(registryFile);
         if (installed == null)
         {
            return Collections.emptyList();
         }
         List<Node> list = installed.get("addon");
         for (Node addon : list)
         {
            AddonEntry entry = AddonEntry.from(addon.getAttribute(ATTR_NAME),
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

   public synchronized boolean install(AddonEntry addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("Addon must not be null");

      }
      if (Strings.isNullOrEmpty(addon.getName()))
      {
         throw new RuntimeException("Addon name must not be null");
      }
      if (Strings.isNullOrEmpty(addon.getApiVersion()))
      {
         throw new RuntimeException("Addon API version must not be null");
      }
      if (Strings.isNullOrEmpty(addon.getSlot()))
      {
         addon = AddonEntry.from(addon.getName(), addon.getApiVersion(), addon.getSlot());
      }

      List<AddonEntry> installedAddons = listInstalled();
      for (AddonEntry e : installedAddons)
      {
         if (addon.getName().equals(e.getName()))
         {
            remove(e);
         }
      }

      File registryFile = getRegistryFile();
      try
      {
         Node installed = XMLParser.parse(registryFile);

         installed.getOrCreate(
                  "addon@" + ATTR_NAME + "=" + addon.getName() + "&" + ATTR_API_VERSION + "=" + addon.getApiVersion())
                  .attribute(ATTR_SLOT, addon.getSlot());
         Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));

         return true;
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException("Could not read [" + registryFile.getAbsolutePath()
                  + "] - ", e);
      }
   }

   public synchronized boolean remove(final AddonEntry addon)
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
            Node installed = XMLParser.parse(registryFile);

            Node child = installed.getSingle("addon@" + ATTR_NAME + "=" + addon.getName() + "&"
                     + ATTR_API_VERSION
                     + "=" + addon.getApiVersion());
            installed.removeChild(child);
            Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));
            return true;
         }
         catch (FileNotFoundException e)
         {
         }
      }
      return false;
   }

   public synchronized AddonEntry get(final AddonEntry addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("Addon must not be null");
      }

      File registryFile = getRegistryFile();
      try
      {
         Node installed = XMLParser.parse(registryFile);

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
                     return AddonEntry.from(child.getAttribute(ATTR_NAME),
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

   public synchronized File getAddonResourceDir(AddonEntry found)
   {
      Assert.notNull(found.getSlot(), "Addon slot must be specified.");
      Assert.notNull(found.getName(), "Addon name must be specified.");

      String path = found.getName().replaceAll("\\.", "/");
      File addonDir = new File(getRepositoryDirectory(), path + "/" + found.getSlot());
      return addonDir;
   }

   public synchronized File getAddonBaseDir(AddonEntry found)
   {
      Assert.notNull(found.getSlot(), "Addon slot must be specified.");
      Assert.notNull(found.getName(), "Addon name must be specified.");

      String path = found.getName().split("\\.")[0];
      File addonDir = new File(getRepositoryDirectory(), path);
      return addonDir;
   }

   public synchronized boolean has(final AddonEntry addon)
   {
      return get(addon) != null;
   }

   public List<File> getAddonResources(AddonEntry found)
   {
      File dir = getAddonResourceDir(found);
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

   public File getAddonSlotDir(AddonEntry addon)
   {
      return new File(getAddonBaseDir(addon).getAbsolutePath(), addon.getSlot());
   }

   public synchronized List<AddonDependency> getAddonDependencies(AddonEntry addon)
   {
      List<AddonDependency> result = new ArrayList<AddonDependency>();
      File descriptor = getAddonDescriptor(addon);

      try
      {
         Node installed = XMLParser.parse(descriptor);

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

   public synchronized File getAddonDescriptor(AddonEntry addon)
   {
      File descriptorFile = new File(getAddonResourceDir(addon), FAR_DESCRIPTOR_FILENAME);
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

   @Override
   public synchronized AddonEntry deploy(AddonEntry entry, File farFile, File... dependencies)
   {
      File addonSlotDir = getAddonSlotDir(entry);
      try
      {
         Files.copyFileToDirectory(farFile, addonSlotDir);
         for (File dependency : dependencies)
         {
            Files.copyFileToDirectory(dependency, addonSlotDir);
         }
      }
      catch (IOException io)
      {
         throw new AddonDeploymentException("Could not deploy addon " + entry, io);
      }
      return entry;
   }
}

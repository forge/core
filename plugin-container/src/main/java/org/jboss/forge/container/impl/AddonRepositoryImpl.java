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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.Typed;

import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonEntry;
import org.jboss.forge.container.AddonRepository;
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
   private static final String ATTR_VERSION = "version";

   private static final String ATTR_API_VERSION = "api-version";

   private static final String ATTR_NAME = "name";

   private static final String ADDON_DIR_DEFAULT = ".forge/addons";

   private static final String REGISTRY_FILE_NAME = "installed.xml";
   private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(\\.|-)(.*)");
   private static final String FAR_DESCRIPTOR_FILENAME = "forge.xml";

   public static AddonRepository forAddonDir(File dir)
   {
      return new AddonRepositoryImpl(dir);
   }

   public static AddonRepository forDefaultAddonDir()
   {
      return new AddonRepositoryImpl(new File(OSUtils.getUserHomePath(), ADDON_DIR_DEFAULT));
   }

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
      Assert.notNull(entry, "Addon entry must not be null.");

      return isApiCompatible(runtimeVersion, entry.getApiVersion());
   }

   /**
    * This method only returns true if:
    * 
    * - The major version of addonApiVersion is equal to the major version of runtimeVersion AND
    * 
    * - The minor version of addonApiVersion is less or equal to the minor version of runtimeVersion
    * 
    * - The addonApiVersion is null
    * 
    * @param runtimeVersion a version in the format x.x.x
    * @param addonApiVersion a version in the format x.x.x
    */
   public static boolean isApiCompatible(CharSequence runtimeVersion, CharSequence addonApiVersion)
   {
      if (addonApiVersion == null || addonApiVersion.length() == 0
               || runtimeVersion == null || runtimeVersion.length() == 0)
         return true;

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

   private File addonDir;

   private AddonRepositoryImpl(File dir)
   {
      Assert.notNull(dir, "Addon directory must not be null");
      this.addonDir = dir;
   }

   @Override
   public synchronized boolean deploy(AddonEntry entry, File farFile, File... dependencies)
   {
      File addonSlotDir = getAddonBaseDir(entry);
      try
      {
         Files.copyFileToDirectory(farFile, addonSlotDir);

         deployForgeXml(entry, farFile);

         for (File dependency : dependencies)
         {
            Files.copyFileToDirectory(dependency, addonSlotDir);
         }
         return true;
      }
      catch (IOException io)
      {
         // TODO throw exception instead?
         io.printStackTrace();
         return false;
      }
   }

   private void deployForgeXml(AddonEntry entry, File farFile) throws IOException, FileNotFoundException
   {
      JarFile farJar = new JarFile(farFile);
      JarEntry forgeXmlEntry = farJar.getJarEntry("META-INF/forge.xml");
      InputStream forgeXml = farJar.getInputStream(forgeXmlEntry);

      if (forgeXml != null)
      {
         File descriptor = getAddonDescriptor(entry);
         FileOutputStream fos = new FileOutputStream(descriptor);
         Streams.write(forgeXml, fos);
      }
   }

   public synchronized boolean disable(final AddonEntry addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("Addon must not be null");
      }

      File registryFile = getRepositoryRegistryFile();
      if (registryFile.exists())
      {
         try
         {
            Node installed = XMLParser.parse(registryFile);

            Node child = installed.getSingle("addon@" + ATTR_NAME + "=" + addon.getName() + "&"
                     + ATTR_VERSION + "=" + addon.getVersion());
            installed.removeChild(child);
            Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));
            return true;
         }
         catch (FileNotFoundException e)
         {
            throw new RuntimeException("Could not read [" + registryFile.getAbsolutePath() + "] - ", e);
         }
      }
      return false;
   }

   public synchronized boolean enable(AddonEntry addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("Addon must not be null");
      }
      if (Strings.isNullOrEmpty(addon.getName()))
      {
         throw new RuntimeException("Addon name must not be null");
      }
      if (Strings.isNullOrEmpty(addon.getVersion()))
      {
         throw new RuntimeException("Addon version must not be null");
      }
      if (Strings.isNullOrEmpty(addon.getApiVersion()))
      {
         addon = AddonEntry.from(addon.getName(), addon.getVersion(), addon.getApiVersion());
      }

      List<AddonEntry> installedAddons = listEnabled();
      for (AddonEntry e : installedAddons)
      {
         if (addon.getName().equals(e.getName()))
         {
            disable(e);
         }
      }

      File registryFile = getRepositoryRegistryFile();
      try
      {
         Node installed = XMLParser.parse(registryFile);

         installed.getOrCreate("addon@" + ATTR_NAME + "=" + (addon.getName() == null ? "" : addon.getName()) +
                  "&" + ATTR_VERSION + "=" + addon.getVersion())
                  .attribute(ATTR_API_VERSION, (addon.getApiVersion() == null ? "" : addon.getApiVersion()));
         Streams.write(XMLParser.toXMLInputStream(installed), new FileOutputStream(registryFile));

         return true;
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException("Could not read [" + registryFile.getAbsolutePath() + "] - ", e);
      }
   }

   public synchronized File getAddonBaseDir(AddonEntry found)
   {
      Assert.notNull(found.getVersion(), "Addon version must be specified.");
      Assert.notNull(found.getName(), "Addon name must be specified.");

      System.out.println(found.toModuleId());
      File addonDir = new File(getRepositoryDirectory(), found.toModuleId().replaceAll("[^a-zA-Z0-9]+", "-"));
      return addonDir;
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
      File descriptorFile = new File(getAddonBaseDir(addon), FAR_DESCRIPTOR_FILENAME);
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

   public List<File> getAddonResources(AddonEntry found)
   {
      File dir = getAddonBaseDir(found);
      if (dir.exists())
      {
         return Arrays.asList(dir.listFiles(new FilenameFilter()
         {
            @Override
            public boolean accept(File file, String name)
            {
               return name.endsWith(".jar") || name.endsWith(".far");
            }
         }));
      }
      return new ArrayList<File>();
   }

   private synchronized AddonEntry getEnabled(final AddonEntry addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("Addon must not be null");
      }

      File registryFile = getRepositoryRegistryFile();
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
                  if ((addon.getVersion() == null)
                           || addon.getVersion().equals(child.getAttribute(ATTR_VERSION)))
                  {
                     return AddonEntry.from(child.getAttribute(ATTR_NAME),
                              child.getAttribute(ATTR_VERSION),
                              child.getAttribute(ATTR_API_VERSION));
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

   public synchronized File getRepositoryRegistryFile()
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

   public synchronized boolean isEnabled(final AddonEntry addon)
   {
      return getEnabled(addon) != null;
   }

   public synchronized List<AddonEntry> listEnabled()
   {
      List<AddonEntry> result = new ArrayList<AddonEntry>();
      File registryFile = getRepositoryRegistryFile();
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
                     addon.getAttribute(ATTR_VERSION),
                     addon.getAttribute(ATTR_API_VERSION));
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

   public synchronized List<AddonEntry> listEnabledCompatibleWithVersion(final String version)
   {
      List<AddonEntry> list = listEnabled();
      List<AddonEntry> result = list;

      result = new ArrayList<AddonEntry>();
      for (AddonEntry entry : list)
      {
         if (isApiCompatible(version, entry))
         {
            result.add(entry);
         }
      }
      return result;
   }
}

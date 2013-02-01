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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.Typed;

import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
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
   private static final String ATTR_API_VERSION = "api-version";
   private static final String ATTR_EXPORT = "export";
   private static final String ATTR_NAME = "name";
   private static final String ATTR_OPTIONAL = "optional";
   private static final String ATTR_VERSION = "version";

   private static final String DEFAULT_ADDON_DIR = ".forge/addons";
   private static final String REGISTRY_DESCRIPTOR_NAME = "installed.xml";
   private static final String ADDON_DESCRIPTOR_FILENAME = "addon.xml";

   private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(\\.|-)(.*)");

   // FIXME Enhance this synchronization/locking with actual NIO file locking
   private static Object lock = new Object();

   public static AddonRepository forDirectory(File dir)
   {
      return new AddonRepositoryImpl(dir);
   }

   public static AddonRepository forDefaultDirectory()
   {
      return new AddonRepositoryImpl(new File(OSUtils.getUserHomePath(), DEFAULT_ADDON_DIR));
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

   public static boolean isApiCompatible(CharSequence runtimeVersion, AddonId entry)
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
   public boolean deploy(AddonId addon, List<AddonDependency> dependencies, List<File> resourceJars)
   {
      File addonSlotDir = getAddonBaseDir(addon);
      File descriptor = getAddonDescriptor(addon);
      try
      {
         synchronized (lock)
         {
            if (resourceJars != null)
               for (File jar : resourceJars)
               {
                  Files.copyFileToDirectory(jar, addonSlotDir);
               }

            /*
             * Write out the addon module dependency configuration
             */
            Node addonXml = XMLParser.parse(descriptor);
            Node dependenciesNode = addonXml.createChild("dependencies");

            if (dependencies != null)
               for (AddonDependency dependency : dependencies)
               {
                  Node dep = dependenciesNode.createChild("dependency");
                  dep.attribute(ATTR_NAME, dependency.getId().getName());
                  dep.attribute(ATTR_VERSION, dependency.getId().getVersion());
                  dep.attribute(ATTR_EXPORT, dependency.isExport());
                  dep.attribute(ATTR_OPTIONAL, dependency.isOptional());
               }

            FileOutputStream fos = null;
            try
            {
               fos = new FileOutputStream(descriptor);
               Streams.write(XMLParser.toXMLInputStream(addonXml), fos);
            }
            finally
            {
               Streams.closeQuietly(fos);
            }
            return true;
         }
      }
      catch (IOException io)
      {
         io.printStackTrace();
         return false;
      }
   }

   @Override
   public boolean disable(final AddonId addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("Addon must not be null");
      }

      File registryFile = getRepositoryRegistryFile();
      synchronized (lock)
      {
         if (registryFile.exists())
         {
            try
            {
               Node installed = XMLParser.parse(registryFile);

               Node child = installed.getSingle("addon@" + ATTR_NAME + "=" + addon.getName() + "&"
                        + ATTR_VERSION + "=" + addon.getVersion());
               installed.removeChild(child);
               FileOutputStream outStream = null;
               try
               {
                  outStream = new FileOutputStream(registryFile);
                  Streams.write(XMLParser.toXMLInputStream(installed), outStream);
               }
               finally
               {
                  Streams.closeQuietly(outStream);
               }
               return true;
            }
            catch (IOException e)
            {
               throw new RuntimeException("Could not modify [" + registryFile.getAbsolutePath() + "] - ", e);
            }
         }
      }
      return false;
   }

   @Override
   public boolean enable(AddonId addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("AddonId must not be null");
      }

      if (Strings.isNullOrEmpty(addon.getApiVersion()))
      {
         addon = AddonId.from(addon.getName(), addon.getVersion(), addon.getApiVersion());
      }

      File registryFile = getRepositoryRegistryFile();
      synchronized (lock)
      {
         try
         {
            Node installed = XMLParser.parse(registryFile);

            installed.getOrCreate("addon@" + ATTR_NAME + "=" + (addon.getName() == null ? "" : addon.getName()) +
                     "&" + ATTR_VERSION + "=" + addon.getVersion())
                     .attribute(ATTR_API_VERSION, (addon.getApiVersion() == null ? "" : addon.getApiVersion()));
            FileOutputStream destination = null;
            try
            {
               destination = new FileOutputStream(registryFile);
               Streams.write(XMLParser.toXMLInputStream(installed), destination);
            }
            finally
            {
               Streams.closeQuietly(destination);
            }

            return true;
         }
         catch (FileNotFoundException e)
         {
            throw new RuntimeException("Could not read [" + registryFile.getAbsolutePath() + "] - ", e);
         }
      }
   }

   @Override
   public File getAddonBaseDir(AddonId found)
   {
      Assert.notNull(found, "Addon must be specified.");
      Assert.notNull(found.getVersion(), "Addon version must be specified.");
      Assert.notNull(found.getName(), "Addon name must be specified.");

      File addonDir = new File(getRepositoryDirectory(), found.toCoordinates().replaceAll("[^a-zA-Z0-9]+", "-"));
      return addonDir;
   }

   @Override
   public Set<AddonDependency> getAddonDependencies(AddonId addon)
   {
      Set<AddonDependency> result = new HashSet<AddonDependency>();
      File descriptor = getAddonDescriptor(addon);

      synchronized (lock)
      {
         try
         {
            Node installed = XMLParser.parse(descriptor);

            List<Node> children = installed.get("dependencies/dependency");
            for (Node child : children)
            {
               if (child != null)
               {
                  result.add(AddonDependency.create(AddonId.from(child.getAttribute(ATTR_NAME),
                           child.getAttribute(ATTR_VERSION)),
                           Boolean.valueOf(child.getAttribute(ATTR_EXPORT)),
                           Boolean.valueOf(child.getAttribute(ATTR_OPTIONAL))));
               }
            }
         }
         catch (FileNotFoundException e)
         {
            // already removed
         }

         return result;
      }
   }

   @Override
   public File getAddonDescriptor(AddonId addon)
   {
      File descriptorFile = getAddonDescriptorFile(addon);
      synchronized (lock)
      {
         try
         {
            if (!descriptorFile.exists())
            {
               descriptorFile.mkdirs();
               descriptorFile.delete();
               descriptorFile.createNewFile();

               FileOutputStream stream = null;
               try
               {
                  stream = new FileOutputStream(descriptorFile);
                  Streams.write(XMLParser.toXMLInputStream(XMLParser.parse("<addon/>")), stream);
               }
               finally
               {
                  Streams.closeQuietly(stream);
               }
            }
            return descriptorFile;
         }
         catch (Exception e)
         {
            throw new RuntimeException("Error initializing addon descriptor file.", e);
         }
      }
   }

   private File getAddonDescriptorFile(AddonId addon)
   {
      return new File(getAddonBaseDir(addon), ADDON_DESCRIPTOR_FILENAME);
   }

   @Override
   public List<File> getAddonResources(AddonId found)
   {
      File dir = getAddonBaseDir(found);
      synchronized (lock)
      {
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
   }

   private synchronized AddonId getEnabled(final AddonId addon)
   {
      if (addon == null)
      {
         throw new RuntimeException("Addon must not be null");
      }

      File registryFile = getRepositoryRegistryFile();
      synchronized (lock)
      {
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
                        return AddonId.from(child.getAttribute(ATTR_NAME),
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
      }

      return null;
   }

   @Override
   public File getRepositoryDirectory()
   {
      if (!addonDir.exists() || !addonDir.isDirectory())
      {
         synchronized (lock)
         {
            addonDir.delete();
            System.gc();
            if (!addonDir.mkdirs())
            {
               throw new RuntimeException("Could not create Addon Directory [" + addonDir + "]");
            }
         }
      }
      return addonDir;
   }

   @Override
   public File getRepositoryRegistryFile()
   {
      File registryFile = new File(getRepositoryDirectory(), REGISTRY_DESCRIPTOR_NAME);
      try
      {
         synchronized (lock)
         {
            if (!registryFile.exists())
            {
               registryFile.createNewFile();

               FileOutputStream out = null;
               try
               {
                  out = new FileOutputStream(registryFile);
                  Streams.write(XMLParser.toXMLInputStream(XMLParser.parse("<installed></installed>")), out);
               }
               finally
               {
                  Streams.closeQuietly(out);
               }
            }
         }
         return registryFile;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error initializing addon registry file [" + registryFile + "]", e);
      }
   }

   @Override
   public boolean isDeployed(AddonId addon)
   {
      return getAddonBaseDir(addon).exists() && getAddonDescriptorFile(addon).exists()
               && !getAddonResources(addon).isEmpty();
   }

   @Override
   public boolean isEnabled(final AddonId addon)
   {
      return getEnabled(addon) != null;
   }

   @Override
   public synchronized List<AddonId> listEnabled()
   {
      List<AddonId> result = new ArrayList<AddonId>();
      File registryFile = getRepositoryRegistryFile();
      try
      {
         synchronized (lock)
         {
            Node installed = XMLParser.parse(registryFile);
            if (installed == null)
            {
               return Collections.emptyList();
            }
            List<Node> list = installed.get("addon");
            for (Node addon : list)
            {
               AddonId entry = AddonId.from(addon.getAttribute(ATTR_NAME),
                        addon.getAttribute(ATTR_VERSION),
                        addon.getAttribute(ATTR_API_VERSION));
               result.add(entry);
            }
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

   @Override
   public List<AddonId> listEnabledCompatibleWithVersion(final String version)
   {
      List<AddonId> list = listEnabled();
      List<AddonId> result = list;

      result = new ArrayList<AddonId>();
      for (AddonId entry : list)
      {
         if (isApiCompatible(version, entry))
         {
            result.add(entry);
         }
      }
      return result;
   }

   @Override
   public boolean undeploy(AddonId addon)
   {
      File dir = getAddonBaseDir(addon);
      synchronized (lock)
      {
         return Files.delete(dir, true);
      }
   }
}

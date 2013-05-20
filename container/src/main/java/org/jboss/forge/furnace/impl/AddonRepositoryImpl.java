/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Typed;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.lock.LockMode;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.Files;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.furnace.versions.Versions;
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
public final class AddonRepositoryImpl implements MutableAddonRepository
{
   private static final Logger logger = Logger.getLogger(AddonRepositoryImpl.class.getName());

   private static final String ATTR_API_VERSION = "api-version";
   private static final String ATTR_EXPORT = "export";
   private static final String ATTR_NAME = "name";
   private static final String ATTR_OPTIONAL = "optional";
   private static final String ATTR_VERSION = "version";

   private static final String DEFAULT_ADDON_DIR = ".forge/addons";
   private static final String REGISTRY_DESCRIPTOR_NAME = "installed.xml";
   private static final String ADDON_DESCRIPTOR_FILENAME = "addon.xml";

   private static final String DEPENDENCY_TAG_NAME = "dependency";
   private static final String DEPENDENCIES_TAG_NAME = "dependencies";

   private LockManager lock;

   public static MutableAddonRepository forDirectory(Furnace forge, File dir)
   {
      return new AddonRepositoryImpl(forge.getLockManager(), dir);
   }

   public static MutableAddonRepository forDefaultDirectory(Furnace forge)
   {
      return new AddonRepositoryImpl(forge.getLockManager(), new File(OperatingSystemUtils.getUserHomePath(),
               DEFAULT_ADDON_DIR));
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

   public static boolean isApiCompatible(Version runtimeVersion, AddonId entry)
   {
      Assert.notNull(entry, "Addon entry must not be null.");

      return Versions.isApiCompatible(runtimeVersion, entry.getApiVersion());
   }

   private File addonDir;

   private int version = 1;

   private AddonRepositoryImpl(LockManager lock, File dir)
   {
      // TODO Assert.notNull(lock, "LockManager must not be null.");
      Assert.notNull(dir, "Addon directory must not be null.");
      this.addonDir = dir;
      this.lock = lock;
   }

   @Override
   public boolean deploy(final AddonId addon, final List<AddonDependencyEntry> dependencies, final List<File> resources)
   {
      return lock.performLocked(LockMode.WRITE, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            File addonSlotDir = getAddonBaseDir(addon);
            File descriptor = getAddonDescriptor(addon);
            try
            {
               if (resources != null && !resources.isEmpty())
               {
                  for (File resource : resources)
                  {
                     if (resource.isDirectory())
                     {
                        String child = addon.getName()
                                 + resource.getParentFile().getParentFile().getName();
                        child = OperatingSystemUtils.getSafeFilename(child);
                        Files.copyDirectory(resource, new File(addonSlotDir, child));
                     }
                     else
                     {
                        Files.copyFileToDirectory(resource, addonSlotDir);
                     }
                  }
               }
               /*
                * Write out the addon module dependency configuration
                */
               Node addonXml = getXmlRoot(descriptor);
               Node dependenciesNode = addonXml.getOrCreate(DEPENDENCIES_TAG_NAME);

               for (AddonDependencyEntry dependency : dependencies)
               {
                  String name = dependency.getId().getName();
                  Node dep = null;
                  for (Node node : dependenciesNode.get(DEPENDENCY_TAG_NAME))
                  {
                     if (name.equals(node.getAttribute(ATTR_NAME)))
                     {
                        dep = node;
                        break;
                     }
                  }
                  if (dep == null)
                  {
                     dep = dependenciesNode.createChild(DEPENDENCY_TAG_NAME);
                     dep.attribute(ATTR_NAME, name);
                  }
                  dep.attribute(ATTR_VERSION, dependency.getId().getVersion());
                  dep.attribute(ATTR_EXPORT, dependency.isExported());
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
            catch (IOException io)
            {
               io.printStackTrace();
               return false;
            }
         }
      });
   }

   @Override
   public boolean disable(final AddonId addon)
   {
      return lock.performLocked(LockMode.WRITE, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
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
                  Node installed = getXmlRoot(registryFile);

                  Node child = installed.getSingle("addon@" + ATTR_NAME + "=" + addon.getName() + "&"
                           + ATTR_VERSION + "=" + addon.getVersion());
                  installed.removeChild(child);
                  saveRegistryFile(installed);
                  return true;
               }
               catch (IOException e)
               {
                  throw new RuntimeException("Could not modify [" + registryFile.getAbsolutePath() + "] - ", e);
               }
            }
            return false;
         }
      });
   }

   @Override
   public boolean enable(final AddonId addon)
   {
      return lock.performLocked(LockMode.WRITE, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            if (addon == null)
            {
               throw new RuntimeException("AddonId must not be null");
            }

            File registryFile = getRepositoryRegistryFile();
            try
            {
               Node installed = getXmlRoot(registryFile);

               installed.getOrCreate("addon@" + ATTR_NAME + "=" + (addon.getName() == null ? "" : addon.getName()) +
                        "&" + ATTR_VERSION + "=" + addon.getVersion())
                        .attribute(ATTR_API_VERSION, (addon.getApiVersion() == null ? "" : addon.getApiVersion()));

               saveRegistryFile(installed);
               return true;
            }
            catch (FileNotFoundException e)
            {
               throw new RuntimeException("Could not read [" + registryFile.getAbsolutePath() + "] - ", e);
            }
         }
      });
   }

   @Override
   public File getAddonBaseDir(final AddonId found)
   {
      Assert.notNull(found, "Addon must be specified.");
      Assert.notNull(found.getVersion(), "Addon version must be specified.");
      Assert.notNull(found.getName(), "Addon name must be specified.");

      return lock.performLocked(LockMode.READ, new Callable<File>()
      {
         @Override
         public File call() throws Exception
         {

            File addonDir = new File(getRootDirectory(), found.toCoordinates().replaceAll("[^a-zA-Z0-9]+", "-"));
            return addonDir;
         }
      });
   }

   @Override
   public Set<AddonDependencyEntry> getAddonDependencies(final AddonId addon)
   {
      return lock.performLocked(LockMode.READ, new Callable<Set<AddonDependencyEntry>>()
      {
         @Override
         public Set<AddonDependencyEntry> call() throws Exception
         {
            Set<AddonDependencyEntry> result = new HashSet<AddonDependencyEntry>();
            File descriptor = getAddonDescriptor(addon);

            try
            {
               Node installed = getXmlRoot(descriptor);

               List<Node> children = installed.get("dependencies/dependency");
               for (final Node child : children)
               {
                  if (child != null)
                  {
                     result.add(AddonDependencyEntry.create(AddonId.from(child.getAttribute(ATTR_NAME),
                              child.getAttribute(ATTR_VERSION)), Boolean.valueOf(child.getAttribute(ATTR_EXPORT)),
                              Boolean.valueOf(child.getAttribute(ATTR_OPTIONAL)))
                              );
                  }
               }
            }
            catch (FileNotFoundException e)
            {
               // already removed
            }

            return result;
         }
      });
   }

   @Override
   public File getAddonDescriptor(final AddonId addon)
   {
      return lock.performLocked(LockMode.READ, new Callable<File>()
      {
         @Override
         public File call() throws Exception
         {
            File descriptorFile = getAddonDescriptorFile(addon);
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
      });
   }

   private File getAddonDescriptorFile(final AddonId addon)
   {
      return lock.performLocked(LockMode.READ, new Callable<File>()
      {

         @Override
         public File call() throws Exception
         {
            return new File(getAddonBaseDir(addon), ADDON_DESCRIPTOR_FILENAME);
         }
      });
   }

   @Override
   public List<File> getAddonResources(final AddonId found)
   {
      return lock.performLocked(LockMode.READ, new Callable<List<File>>()
      {
         @Override
         public List<File> call() throws Exception
         {
            File dir = getAddonBaseDir(found);
            if (dir.exists())
            {
               File[] files = dir.listFiles(new FileFilter()
               {
                  @Override
                  public boolean accept(File pathname)
                  {
                     return pathname.isDirectory() || pathname.getName().endsWith(".jar");
                  }
               });
               return Arrays.asList(files);
            }
            return Collections.emptyList();
         }
      });
   }

   private AddonId getEnabled(final AddonId addon)
   {
      return lock.performLocked(LockMode.READ, new Callable<AddonId>()
      {
         @Override
         public AddonId call() throws Exception
         {
            if (addon == null)
            {
               throw new RuntimeException("Addon must not be null");
            }

            File registryFile = getRepositoryRegistryFile();
            try
            {
               Node installed = getXmlRoot(registryFile);

               List<Node> children = installed.get("addon@" + ATTR_NAME + "=" + addon.getName());
               for (Node child : children)
               {
                  if (child != null)
                  {
                     if ((addon.getApiVersion() == null)
                              || Versions.areEqual(new SingleVersion(child.getAttribute(ATTR_API_VERSION)),
                                       addon.getApiVersion()))
                     {
                        if ((addon.getVersion() == null)
                                 || Versions.areEqual(new SingleVersion(child.getAttribute(ATTR_VERSION)),
                                          addon.getVersion()))
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
            return null;
         }
      });
   }

   @Override
   public File getRootDirectory()
   {
      return lock.performLocked(LockMode.READ, new Callable<File>()
      {
         @Override
         public File call() throws Exception
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
      });
   }

   private File getRepositoryRegistryFile()
   {
      return lock.performLocked(LockMode.READ, new Callable<File>()
      {
         @Override
         public File call() throws Exception
         {
            File registryFile = new File(getRootDirectory(), REGISTRY_DESCRIPTOR_NAME);
            try
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
               return registryFile;
            }
            catch (Exception e)
            {
               throw new RuntimeException("Error initializing addon registry file [" + registryFile + "]", e);
            }
         }
      });
   }

   @Override
   public boolean isDeployed(final AddonId addon)
   {
      return lock.performLocked(LockMode.READ, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            File addonBaseDir = getAddonBaseDir(addon);
            File addonDescriptorFile = getAddonDescriptorFile(addon);
            List<File> addonResources = getAddonResources(addon);

            return addonBaseDir.exists() && addonDescriptorFile.exists() && !addonResources.isEmpty();
         }
      });
   }

   @Override
   public boolean isEnabled(final AddonId addon)
   {
      return getEnabled(addon) != null;
   }

   @Override
   public List<AddonId> listEnabled()
   {
      return lock.performLocked(LockMode.READ, new Callable<List<AddonId>>()
      {
         @Override
         public List<AddonId> call() throws Exception
         {
            List<AddonId> result = new ArrayList<AddonId>();
            File registryFile = getRepositoryRegistryFile();
            try
            {
               Node installed = getXmlRoot(registryFile);
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
            catch (XMLParserException e)
            {
               throw new RuntimeException("Invalid syntax in [" + registryFile.getAbsolutePath()
                        + "] - Please delete this file and restart Furnace", e);
            }
            catch (FileNotFoundException e)
            {
               // this is OK, no addons installed
            }
            return result;
         }
      });
   }

   @Override
   public List<AddonId> listEnabledCompatibleWithVersion(final String version)
   {
      return lock.performLocked(LockMode.READ, new Callable<List<AddonId>>()
      {
         @Override
         public List<AddonId> call() throws Exception
         {
            List<AddonId> list = listEnabled();
            List<AddonId> result = list;

            result = new ArrayList<AddonId>();
            for (AddonId entry : list)
            {
               if (version == null || entry.getApiVersion() == null
                        || Versions.isApiCompatible(new SingleVersion(version), entry.getApiVersion()))
               {
                  result.add(entry);
               }
            }
            return result;
         }
      });
   }

   @Override
   public boolean undeploy(final AddonId addon)
   {
      return lock.performLocked(LockMode.WRITE, new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            File dir = getAddonBaseDir(addon);
            disable(addon);
            return Files.delete(dir, true);
         }
      });
   }

   private Node getXmlRoot(File registryFile) throws FileNotFoundException, InterruptedException
   {
      Node installed = null;

      while (installed == null)
      {
         try
         {
            installed = XMLParser.parse(registryFile);
         }
         catch (XMLParserException e)
         {
            logger.log(Level.WARNING, "Error occurred while parsing [" + registryFile + "]", e);
         }
      }

      return installed;
   }

   @Override
   public Date getLastModified()
   {
      return lock.performLocked(LockMode.READ, new Callable<Date>()
      {
         @Override
         public Date call() throws Exception
         {
            return new Date(getRepositoryRegistryFile().lastModified());
         }
      });
   }

   @Override
   public int getVersion()
   {
      return version;
   }

   private void saveRegistryFile(Node installed) throws FileNotFoundException
   {
      FileOutputStream outStream = null;
      try
      {
         // TODO need to replace this with actual file-system transactionality, but should work for the common case
         outStream = new FileOutputStream(getRepositoryRegistryFile());
         incrementVersion();
         Streams.write(XMLParser.toXMLInputStream(installed), outStream);
      }
      finally
      {
         Streams.closeQuietly(outStream);
      }
   }

   private void incrementVersion()
   {
      version++;
   }

   @Override
   public String toString()
   {
      return getRootDirectory().getAbsolutePath();
   }

}

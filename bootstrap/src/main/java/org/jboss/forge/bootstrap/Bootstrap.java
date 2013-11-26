/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.impl.addons.AddonRepositoryImpl;
import org.jboss.forge.furnace.manager.impl.AddonManagerImpl;
import org.jboss.forge.furnace.manager.maven.addon.MavenAddonDependencyResolver;
import org.jboss.forge.furnace.manager.request.AddonActionRequest;
import org.jboss.forge.furnace.manager.spi.AddonDependencyResolver;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.furnace.versions.Versions;

/**
 * A class with a main method to bootstrap Furnace.
 * 
 * You can deploy addons by calling {@link Bootstrap#install(String)}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class Bootstrap
{

   private final Furnace furnace;
   private boolean exitAfter = false;

   public static void main(final String[] args)
   {
      final List<String> bootstrapArgs = new ArrayList<String>();
      final Properties systemProperties = System.getProperties();
      // Set system properties
      for (String arg : args)
      {
         if (arg.startsWith("-D"))
         {
            final String name;
            final String value;
            final int index = arg.indexOf("=");
            if (index == -1)
            {
               name = arg.substring(2);
               value = "true";
            }
            else
            {
               name = arg.substring(2, index);
               value = arg.substring(index + 1);
            }
            systemProperties.setProperty(name, value);
         }
         else
         {
            bootstrapArgs.add(arg);
         }
      }

      // Check for the forge log directory
      final String logDir = systemProperties.getProperty("org.jboss.forge.log.file",
               new File(OperatingSystemUtils.getUserForgeDir(), "log/forge.log").getAbsolutePath());
      // Ensure this value is always set
      systemProperties.setProperty("org.jboss.forge.log.file", logDir);

      // Look for a logmanager before any logging takes place
      final String logManagerName = getServiceName(Bootstrap.class.getClassLoader(), "java.util.logging.LogManager");
      if (logManagerName != null)
      {
         systemProperties.setProperty("java.util.logging.manager", logManagerName);
      }
      Bootstrap bootstrap = new Bootstrap(bootstrapArgs.toArray(new String[bootstrapArgs.size()]));
      bootstrap.start();
   }

   private Bootstrap(String[] args)
   {
      boolean listInstalled = false;
      String installAddon = null;
      String removeAddon = null;
      furnace = ServiceLoader.load(Furnace.class).iterator().next();

      furnace.setArgs(args);

      if (args.length > 0)
      {
         for (int i = 0; i < args.length; i++)
         {
            if ("--install".equals(args[i]) || "-i".equals(args[i]))
            {
               installAddon = args[++i];
            }
            else if ("--remove".equals(args[i]) || "-r".equals(args[i]))
            {
               removeAddon = args[++i];
            }
            else if ("--list".equals(args[i]) || "-l".equals(args[i]))
            {
               listInstalled = true;
            }
            else if ("--addonDir".equals(args[i]) || "-a".equals(args[i]))
            {
               furnace.addRepository(AddonRepositoryMode.MUTABLE, new File(args[++i]));
            }
            else if ("--batchMode".equals(args[i]) || "-b".equals(args[i]))
            {
               furnace.setServerMode(false);
            }
            else if ("--debug".equals(args[i]) || "-d".equals(args[i]))
            {
               // This is just to avoid the Unknown option: --debug message below
            }
            else if ("--version".equals(args[i]) || "-v".equals(args[i]))
            {
               System.out.println("Forge version " + AddonRepositoryImpl.getRuntimeAPIVersion());
               exitAfter = true;
            }
            else
               System.out.println("Unknown option: " + args[i]);
         }
      }

      if (furnace.getRepositories().isEmpty())
         furnace.addRepository(AddonRepositoryMode.MUTABLE, new File(OperatingSystemUtils.getUserForgeDir(), "addons"));
      if (listInstalled)
         list();
      if (installAddon != null)
         install(installAddon);
      if (removeAddon != null)
         remove(removeAddon);
   }

   private void list()
   {
      try
      {
         for (AddonRepository repository : furnace.getRepositories())
         {
            System.out.println(repository.getRootDirectory().getCanonicalPath() + ":");
            List<AddonId> addons = repository.listEnabled();
            for (AddonId addon : addons)
            {
               System.out.println(addon.toCoordinates());
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.out.println("> Forge version [" + AddonRepositoryImpl.getRuntimeAPIVersion() + "]");
      }
      finally
      {
         exitAfter = true;
      }
   }

   private void start()
   {
      if (!exitAfter)
         furnace.start();
   }

   private void install(String addonCoordinates)
   {
      Version runtimeAPIVersion = AddonRepositoryImpl.getRuntimeAPIVersion();
      try
      {
         AddonDependencyResolver resolver = new MavenAddonDependencyResolver();
         AddonManagerImpl addonManager = new AddonManagerImpl(furnace, resolver);

         AddonId addon;
         // This allows forge --install maven
         if (addonCoordinates.contains(","))
         {
            addon = AddonId.fromCoordinates(addonCoordinates);
         }
         else
         {
            String coordinate = "org.jboss.forge.addon:" + addonCoordinates;
            AddonId[] versions = resolver.resolveVersions(coordinate).get();
            if (versions.length == 0)
            {
               throw new IllegalArgumentException("No Artifact version found for " + coordinate);
            }
            else
            {
               AddonId selected = null;
               for (int i = versions.length - 1; selected == null && i >= 0; i--)
               {
                  String apiVersion = resolver.resolveAPIVersion(versions[i]).get();
                  if (apiVersion != null && Versions.isApiCompatible(runtimeAPIVersion, new SingleVersion(apiVersion)))
                  {
                     selected = versions[i];
                  }
               }
               if (selected == null)
               {
                  throw new IllegalArgumentException("No compatible addon API version found for " + coordinate
                           + " for API " + runtimeAPIVersion);
               }

               addon = selected;
            }
         }

         // FIXME: May prompt for confirmation
         AddonActionRequest request = addonManager.install(addon);
         System.out.println(request);
         request.perform();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.out.println("> Forge version [" + runtimeAPIVersion + "]");
      }
      finally
      {
         exitAfter = true;
      }
   }

   private void remove(String addonCoordinates)
   {
      try
      {
         AddonId addon = null;
         // This allows forge --remove maven
         if (addonCoordinates.contains(","))
         {
            addon = AddonId.fromCoordinates(addonCoordinates);
         }
         else
         {
            String coordinates = "org.jboss.forge.addon:" + addonCoordinates;
            REPOS: for (AddonRepository repository : furnace.getRepositories())
            {
               for (AddonId id : repository.listEnabled())
               {
                  if (coordinates.equals(id.getName()))
                  {
                     addon = id;
                     if (repository instanceof MutableAddonRepository)
                     {
                        ((MutableAddonRepository) repository).disable(addon);
                        ((MutableAddonRepository) repository).undeploy(addon);
                     }
                     break REPOS;
                  }
               }
            }
            if (addon == null)
            {
               throw new IllegalArgumentException("No addon exists with id " + coordinates);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         System.out.println("> Forge version [" + AddonRepositoryImpl.getRuntimeAPIVersion() + "]");
      }
      finally
      {
         exitAfter = true;
      }
   }

   private static String getServiceName(final ClassLoader classLoader, final String className)
   {
      final InputStream stream = classLoader.getResourceAsStream("META-INF/services/" + className);
      if (stream != null)
      {
         BufferedReader reader = null;
         try
         {
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null)
            {
               final int i = line.indexOf('#');
               if (i != -1)
               {
                  line = line.substring(0, i);
               }
               line = line.trim();
               if (line.length() == 0)
                  continue;
               return line;
            }
         }
         catch (IOException ignored)
         {
            // ignore
         }
         finally
         {
            try
            {
               if (reader != null)
                  reader.close();
            }
            catch (IOException ignored)
            {
               // ignore
            }

            try
            {
               if (stream != null)
                  stream.close();
            }
            catch (IOException e)
            {
               // ignore
            }
         }
      }
      return null;
   }

}

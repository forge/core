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
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.repositories.AddonRepository;
import org.jboss.forge.container.repositories.MutableAddonRepository;
import org.jboss.forge.dependencies.Coordinate;
import org.jboss.forge.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.maven.dependencies.FileResourceFactory;
import org.jboss.forge.maven.dependencies.MavenContainer;
import org.jboss.forge.maven.dependencies.MavenDependencyResolver;

/**
 * A class with a main method to bootstrap Forge.
 *
 * You can deploy addons by calling {@link Bootstrap#install(String)}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class Bootstrap
{

   private final Forge forge;
   private boolean exitAfter = false;

   public static void main(final String[] args)
   {
      // Look for a logmanager before any logging takes place
      final String logManagerName = getServiceName(Bootstrap.class.getClassLoader(), "java.util.logging.LogManager");
      if (logManagerName != null)
      {
         System.setProperty("java.util.logging.manager", logManagerName);
      }
      Bootstrap bootstrap = new Bootstrap(args);
      bootstrap.start();
   }

   private Bootstrap(String[] args)
   {
      final Logger logger = Logger.getLogger(Bootstrap.class.getName());
      boolean listInstalled = false;
      String installAddon = null;
      String removeAddon = null;
      forge = ServiceLoader.load(Forge.class).iterator().next();


      List<AddonRepository> repositories = new ArrayList<AddonRepository>();
      if (args.length > 0)
      {
         for (int i = 0; i < args.length; i++)
         {
            if ("--install".equals(args[i]))
            {
               installAddon = args[++i];
            }
            else if ("--remove".equals(args[i]))
            {
               removeAddon = args[++i];
            }
            else if ("--list".equals(args[i]))
            {
               listInstalled = true;
            }
            else if ("--addonDir".equals(args[i]))
            {
               repositories.add(AddonRepositoryImpl.forDirectory(forge, new File(args[++i])));
            }
            else if ("--batchMode".equals(args[i]))
            {
               forge.setServerMode(false);
            }
            else
               logger.warning("Unknown option: " + args[i]);
         }
      }

      if (!repositories.isEmpty())
         forge.setRepositories(repositories);

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
         for (AddonRepository repository : forge.getRepositories())
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
      }
      finally
      {
         exitAfter = true;
      }
   }

   private void start()
   {
      if (!exitAfter)
         forge.start();
   }

   private void install(String addonCoordinates)
   {
      try
      {
         MavenDependencyResolver resolver = new MavenDependencyResolver(new FileResourceFactory(), new MavenContainer());
         AddonManagerImpl addonManager = new AddonManagerImpl(forge, resolver);

         AddonId addon;
         // This allows forge --install maven
         if (addonCoordinates.contains(","))
         {
            addon = AddonId.fromCoordinates(addonCoordinates);
         }
         else
         {
            String coordinates = "org.jboss.forge:" + addonCoordinates;
            CoordinateBuilder coordinate = CoordinateBuilder.create(coordinates);
            List<Coordinate> versions = resolver.resolveVersions(DependencyQueryBuilder.create(coordinate));
            if (versions.isEmpty())
            {
               throw new IllegalArgumentException("No Artifact version found for " + coordinate);
            }
            Coordinate vCoord = versions.get(versions.size() - 1);
            addon = AddonId.from(vCoord.getGroupId() + ":" + vCoord.getArtifactId(), vCoord.getVersion());
         }

         InstallRequest request = addonManager.install(addon);
         System.out.println(request);
         request.perform();
      }
      catch (Exception e)
      {
         e.printStackTrace();
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
            String coordinates = "org.jboss.forge:" + addonCoordinates;
            REPOS: for (AddonRepository repository : forge.getRepositories())
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
         try
         {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null)
            {
               final int i = line.indexOf('#');
               if (i != -1)
               {
                  line = line.substring(0, i);
               }
               line = line.trim();
               if (line.length() == 0) continue;
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
               stream.close();
            } catch (IOException ignored)
            {
               // ignore
            }
         }
      }
      return null;
   }

}

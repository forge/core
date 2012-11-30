package org.jboss.forge.arquillian;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.deployment.Deployment;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.client.protocol.metadata.Servlet;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.arquillian.protocol.ServletProtocolDescription;
import org.jboss.forge.arquillian.util.ShrinkWrapUtil;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.RegisteredAddon;
import org.jboss.forge.container.Status;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public class ForgeDeployableContainer implements DeployableContainer<ForgeContainerConfiguration>
{
   @Inject
   private Instance<Deployment> deploymentInstance;

   private ForgeRunnable runnable;
   private File addonDir;

   private AddonRepository repository;

   private Map<Deployment, AddonId> deployedAddons = new HashMap<Deployment, AddonId>();
   private Thread thread;

   @Override
   public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException
   {
      AddonId addonToDeploy = getAddonEntry(deploymentInstance.get());
      File destDir = repository.getAddonBaseDir(addonToDeploy);
      destDir.mkdirs();

      if (!(archive instanceof ForgeArchive))
         throw new IllegalArgumentException(
                  "Invalid Archive type. Ensure that your @Deployment method returns type 'ForgeArchive'.");

      ShrinkWrapUtil.toFile(new File(destDir.getAbsolutePath() + "/" + archive.getName()), archive);
      ShrinkWrapUtil.unzip(destDir, archive);

      AddonRegistry registry = runnable.getForge().getAddonRegistry();
      Map<RegisteredAddon, Set<RegisteredAddon>> waitlist = registry.getWaitlistedAddons();

      Set<RegisteredAddon> waitFor = new HashSet<RegisteredAddon>();
      for (RegisteredAddon waiting : waitlist.keySet())
      {
         Set<RegisteredAddon> dependencies = waitlist.get(waiting);
         if (dependencies.size() == 1)
         {
            for (RegisteredAddon dependency : dependencies)
            {
               if (dependency.getId().equals(addonToDeploy))
               {
                  waitFor.add(waiting);
               }
            }
         }
      }

      repository.deploy(addonToDeploy, ((ForgeArchive) archive).getAddonDependencies(), new ArrayList<File>());
      repository.enable(addonToDeploy);

      HTTPContext httpContext = new HTTPContext("localhost", 4141);
      httpContext.add(new Servlet("ArquillianServletRunner", "/ArquillianServletRunner"));

      boolean deployed = false;
      while (!deployed || !waitFor.isEmpty())
      {
         if (thread.isAlive())
         {
            for (RegisteredAddon registeredAddon : registry.getRegisteredAddons())
            {
               if (registeredAddon.getId().equals(addonToDeploy))
               {
                  if (Status.STARTED.equals(registeredAddon.getStatus())
                           || Status.FAILED.equals(registeredAddon.getStatus()))
                  {
                     deployed = true;
                  }
               }
               else if (waitFor.contains(registeredAddon))
               {
                  if (Status.STARTED.equals(registeredAddon.getStatus())
                           || Status.FAILED.equals(registeredAddon.getStatus()))
                  {
                     waitFor.remove(registeredAddon);
                  }
               }
            }
            try
            {
               Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
            }
         }
         else
         {
            break;
         }
      }

      return new ProtocolMetaData()
               .addContext(httpContext);
   }

   @Override
   public void deploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Forge");
   }

   private AddonId getAddonEntry(Deployment deployment)
   {
      if (!deployedAddons.containsKey(deployment))
      {
         String[] coordinates = deployment.getDescription().getName().split(",");
         AddonId entry;
         if (coordinates.length == 3)
            entry = AddonId.from(coordinates[0], coordinates[1], coordinates[2]);
         else if (coordinates.length == 2)
            entry = AddonId.from(coordinates[0], coordinates[1], runnable.getForge().getVersion());
         else if (coordinates.length == 1)
            entry = AddonId.from(coordinates[0], UUID.randomUUID().toString(), runnable.getForge().getVersion());
         else
            entry = AddonId.from(UUID.randomUUID().toString(), UUID.randomUUID().toString(), runnable.getForge()
                     .getVersion());

         deployedAddons.put(deployment, entry);
      }
      return deployedAddons.get(deployment);
   }

   @Override
   public Class<ForgeContainerConfiguration> getConfigurationClass()
   {
      return ForgeContainerConfiguration.class;
   }

   @Override
   public ProtocolDescription getDefaultProtocol()
   {
      return new ServletProtocolDescription();
   }

   @Override
   public void setup(ForgeContainerConfiguration configuration)
   {
   }

   @Override
   public void start() throws LifecycleException
   {
      try
      {
         this.addonDir = File.createTempFile("forge-test-addon-dir", "");
         System.out.println("Executing test case with addon dir [" + addonDir + "]");
         this.repository = AddonRepositoryImpl.forDirectory(addonDir);
      }
      catch (IOException e1)
      {
         throw new LifecycleException("Failed to create temporary addon directory", e1);
      }
      try
      {
         runnable = new ForgeRunnable(addonDir, ClassLoader.getSystemClassLoader());
         thread = new Thread(runnable);
         thread.start();
      }
      catch (Exception e)
      {
         throw new LifecycleException("Could not start Forge runnable.", e);
      }
   }

   @Override
   public void stop() throws LifecycleException
   {
      this.runnable.stop();
   }

   @Override
   public void undeploy(Archive<?> archive) throws DeploymentException
   {
      AddonId addonToUndeploy = getAddonEntry(deploymentInstance.get());
      repository.disable(addonToUndeploy);
      repository.undeploy(addonToUndeploy);

      boolean deployed = true;
      while (deployed)
      {
         deployed = false;
         if (thread.isAlive())
         {
            AddonRegistry registry = runnable.getForge().getAddonRegistry();
            if (registry.isRegistered(addonToUndeploy)
                     && !registry.isWaiting(registry.getRegisteredAddon(addonToUndeploy)))
            {
               deployed = true;
               break;
            }
            try
            {
               Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
            }
         }
      }
   }

   @Override
   public void undeploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Forge");
   }

   private class ForgeRunnable implements Runnable
   {
      private Forge forge;
      private ClassLoader loader;
      private File addonDir;

      public ForgeRunnable(File addonDir, ClassLoader loader)
      {
         this.addonDir = addonDir;
         this.loader = loader;
      }

      public Forge getForge()
      {
         return forge;
      }

      @Override
      public void run()
      {
         ClassLoaders.executeIn(loader, new Callable<Object>()
         {
            @Override
            public Object call() throws Exception
            {
               forge = new Forge();
               forge.setAddonDir(addonDir).start();
               return forge;
            }
         });
      }

      public void stop()
      {
         forge.stop();
         Thread.currentThread().interrupt();
      }

   }
}

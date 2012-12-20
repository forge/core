package org.jboss.forge.arquillian;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.deployment.Deployment;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.forge.addon.dependency.spi.DependencyResolver;
import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.arquillian.archive.ForgeRemoteAddon;
import org.jboss.forge.arquillian.protocol.ForgeProtocolDescription;
import org.jboss.forge.arquillian.util.ShrinkWrapUtil;
import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonFilter;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.Status;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.forge.container.util.Threads;
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
      final AddonId addonToDeploy = getAddonEntry(deploymentInstance.get());
      File destDir = repository.getAddonBaseDir(addonToDeploy);
      destDir.mkdirs();

      if (archive instanceof ForgeArchive)
      {
         ShrinkWrapUtil.toFile(new File(destDir.getAbsolutePath() + "/" + archive.getName()), archive);
         ShrinkWrapUtil.unzip(destDir, archive);

         repository.deploy(addonToDeploy, ((ForgeArchive) archive).getAddonDependencies(), new ArrayList<File>());
         repository.enable(addonToDeploy);
         System.out.println("Deployed [" + addonToDeploy + "]");
      }
      else if (archive instanceof ForgeRemoteAddon)
      {
         ForgeRemoteAddon remoteAddon = (ForgeRemoteAddon) archive;
         AddonManager addonManager = new AddonManager(repository, ServiceLoader.load(DependencyResolver.class)
                  .iterator().next());
         InstallRequest request = addonManager.install(remoteAddon.getAddonId());
         request.perform();
         System.out.println("Deployed [" + remoteAddon.getAddonId() + "]");
      }
      else
      {
         throw new IllegalArgumentException(
                  "Invalid Archive type. Ensure that your @Deployment method returns type 'ForgeArchive'.");
      }

      final AddonRegistry registry = runnable.getForge().getAddonRegistry();
      AddonFilter waitForFilter = new AddonFilter()
      {
         @Override
         public boolean accept(Addon addon)
         {
            boolean result = false;
            Set<AddonDependency> dependencies = repository.getAddonDependencies(addon.getId());
            if (Status.WAITING.equals(addon.getStatus()))
            {
               boolean waitingOnOther = false;
               for (AddonDependency dependency : dependencies)
               {
                  if (!dependency.getId().equals(addonToDeploy) && !registry.isRegistered(dependency.getId()))
                  {
                     waitingOnOther = true;
                  }
               }
               result = !waitingOnOther;
            }
            return result;
         }
      };

      final Set<Addon> waitFor = registry.getRegisteredAddons(waitForFilter);

      boolean deployed = false;
      while (!deployed || !waitFor.isEmpty())
      {
         if (thread.isAlive())
         {
            for (Addon addon : registry.getRegisteredAddons())
            {
               if (addon.getId().equals(addonToDeploy) && isDeploymentComplete(addon))
               {
                  deployed = true;
               }
            }

            Threads.sleep(10);

            if (!waitFor.isEmpty())
               for (Addon addon : registry.getRegisteredAddons())
               {
                  if (waitFor.contains(addon) && isDeploymentComplete(addon))
                  {
                     waitFor.remove(addon);
                  }
               }
         }
         else
         {
            break;
         }
      }

      return new ProtocolMetaData().addContext(runnable.getForge());
   }

   private boolean isDeploymentComplete(Addon addon)
   {
      return Status.STARTED.equals(addon.getStatus())
               || Status.FAILED.equals(addon.getStatus())
               || Status.STOPPED.equals(addon.getStatus())
               || Status.WAITING.equals(addon.getStatus());
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
      return new ForgeProtocolDescription();
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
      AddonRegistry registry = runnable.getForge().getAddonRegistry();

      while (registry.isRegistered(addonToUndeploy)
               && !isDeploymentComplete(registry.getRegisteredAddon(addonToUndeploy))
               && thread.isAlive())
      {
         Threads.sleep(10);
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
               forge.setServerMode(true).setAddonDir(addonDir).start();
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

package org.jboss.forge.arquillian;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.ForgeImpl;
import org.jboss.forge.container.exception.ContainerException;
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
      Deployment deployment = deploymentInstance.get();
      final AddonId addonToDeploy = getAddonEntry(deployment);
      File destDir = repository.getAddonBaseDir(addonToDeploy);
      destDir.mkdirs();

      if (archive instanceof ForgeArchive)
      {
         ShrinkWrapUtil.toFile(new File(destDir.getAbsolutePath() + "/" + archive.getName()), archive);
         ShrinkWrapUtil.unzip(destDir, archive);

         repository.deploy(addonToDeploy, ((ForgeArchive) archive).getAddonDependencies(), new ArrayList<File>());
         repository.enable(addonToDeploy);
         Threads.sleep(200);
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

      AddonRegistry registry = runnable.getForge().getAddonRegistry();

      Addon addon = null;
      while (addon == null)
         addon = registry.getRegisteredAddon(addonToDeploy);

      Future<?> future = registry.start(addon);
      try
      {
         if (!addon.getStatus().isWaiting() && future != null)
         {
            addon = registry.getRegisteredAddon(addonToDeploy);
            future.get();
            if (addon.getStatus().isFailed())
            {
               ContainerException e = new ContainerException("Addon " + addonToDeploy + " failed to deploy.");
               deployment.deployedWithError(e);
               throw e;
            }
         }
      }
      catch (Exception e)
      {
         throw new DeploymentException("Failed to deploy " + addonToDeploy, e);
      }

      return new ProtocolMetaData().addContext(runnable.getForge());
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
            entry = AddonId.from(coordinates[0], coordinates[1]);
         else if (coordinates.length == 1)
            entry = AddonId.from(coordinates[0], UUID.randomUUID().toString());
         else
            entry = AddonId.from(UUID.randomUUID().toString(), UUID.randomUUID().toString());

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
         thread = new Thread(runnable, "Arq-Forge Runtime");
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
      AddonRegistry registry = runnable.getForge().getAddonRegistry();

      try
      {
         registry.stop(registry.getRegisteredAddon(addonToUndeploy));
      }
      catch (Exception e)
      {
         throw new DeploymentException("Failed to undeploy " + addonToUndeploy);
      }
      finally
      {
         repository.disable(addonToUndeploy);
         repository.undeploy(addonToUndeploy);
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
               forge = new ForgeImpl().enableLogging();
               forge.setServerMode(true).setAddonDir(addonDir).start(loader);
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

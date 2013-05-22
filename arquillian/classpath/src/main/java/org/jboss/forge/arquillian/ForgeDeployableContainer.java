package org.jboss.forge.arquillian;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.arquillian.archive.ForgeRemoteAddon;
import org.jboss.forge.arquillian.protocol.ForgeProtocolDescription;
import org.jboss.forge.arquillian.util.ShrinkWrapUtil;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.FurnaceImpl;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.repositories.AddonRepositoryMode;
import org.jboss.forge.furnace.repositories.MutableAddonRepository;
import org.jboss.forge.furnace.util.Addons;
import org.jboss.forge.furnace.util.ClassLoaders;
import org.jboss.forge.furnace.util.Files;
import org.jboss.forge.maven.addon.dependencies.FileResourceFactory;
import org.jboss.forge.maven.addon.dependencies.MavenContainer;
import org.jboss.forge.maven.addon.dependencies.MavenDependencyResolver;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public class ForgeDeployableContainer implements DeployableContainer<ForgeContainerConfiguration>
{
   @Inject
   private Instance<Deployment> deploymentInstance;

   private ForgeRunnable runnable;
   private File addonDir;

   private MutableAddonRepository repository;

   private Map<Deployment, AddonId> deployedAddons = new HashMap<Deployment, AddonId>();
   private Thread thread;

   @Override
   public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException
   {
      Deployment deployment = deploymentInstance.get();
      final AddonId addonToDeploy = getAddonEntry(deployment);
      File destDir = repository.getAddonBaseDir(addonToDeploy);
      destDir.mkdirs();

      System.out.println("Deploying [" + addonToDeploy + "] to repository [" + repository + "]");

      if (archive instanceof ForgeArchive)
      {
         ShrinkWrapUtil.toFile(new File(destDir.getAbsolutePath() + "/" + archive.getName()), archive);
         ShrinkWrapUtil.unzip(destDir, archive);

         repository.deploy(addonToDeploy, ((ForgeArchive) archive).getAddonDependencies(), new ArrayList<File>());
         repository.enable(addonToDeploy);

         while (runnable.furnace.getStatus().isStarting())
         {
            try
            {
               Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
         }
         AddonRegistry registry = runnable.getForge().getAddonRegistry();
         try
         {
            Future<Void> future = registry.getAddon(addonToDeploy).getFuture();
            future.get();
            Addon addon = registry.getAddon(addonToDeploy);
            if (addon.getStatus().isFailed())
            {
               ContainerException e = new ContainerException("Addon " + addonToDeploy + " failed to deploy.");
               deployment.deployedWithError(e);
               throw e;
            }
         }
         catch (Exception e)
         {
            throw new DeploymentException("Failed to deploy " + addonToDeploy, e);
         }
      }
      else if (archive instanceof ForgeRemoteAddon)
      {
         ForgeRemoteAddon remoteAddon = (ForgeRemoteAddon) archive;
         AddonManager addonManager = new AddonManagerImpl(runnable.furnace, new MavenDependencyResolver(
                  new FileResourceFactory(), new MavenContainer()));
         InstallRequest request = addonManager.install(remoteAddon.getAddonId());
         request.perform();
      }
      else
      {
         throw new IllegalArgumentException(
                  "Invalid Archive type. Ensure that your @Deployment method returns type 'ForgeArchive'.");
      }

      return new ProtocolMetaData().addContext(runnable.getForge());
   }

   @Override
   public void deploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Furnace");
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
         this.addonDir = File.createTempFile("furnace", "test-addon-dir");
         runnable = new ForgeRunnable(addonDir, ClassLoader.getSystemClassLoader());
         thread = new Thread(runnable, "Arquillian Furnace Runtime");
         System.out.println("Executing test case with addon dir [" + addonDir + "]");

         thread.start();
      }
      catch (IOException e)
      {
         throw new LifecycleException("Failed to create temporary addon directory", e);
      }
      catch (Exception e)
      {
         throw new LifecycleException("Could not start Furnace runnable.", e);
      }
   }

   @Override
   public void stop() throws LifecycleException
   {
      this.runnable.stop();
      Files.delete(addonDir, true);
   }

   @Override
   public void undeploy(Archive<?> archive) throws DeploymentException
   {
      AddonId addonToUndeploy = getAddonEntry(deploymentInstance.get());
      AddonRegistry registry = runnable.getForge().getAddonRegistry();
      System.out.println("Undeploying [" + addonToUndeploy + "] ... ");

      try
      {
         repository.disable(addonToUndeploy);
         Addon addonToStop = registry.getAddon(addonToUndeploy);
         Addons.waitUntilStopped(addonToStop);
      }
      catch (Exception e)
      {
         throw new DeploymentException("Failed to undeploy " + addonToUndeploy, e);
      }
      finally
      {
         repository.undeploy(addonToUndeploy);
      }
   }

   @Override
   public void undeploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Furnace");
   }

   private class ForgeRunnable implements Runnable
   {
      private Furnace furnace;
      private ClassLoader loader;
      private File addonDir;

      public ForgeRunnable(File addonDir, ClassLoader loader)
      {
         this.furnace = new FurnaceImpl();
         this.addonDir = addonDir;
         this.loader = loader;
      }

      public Furnace getForge()
      {
         return furnace;
      }

      @Override
      public void run()
      {
         try
         {
            ClassLoaders.executeIn(loader, new Callable<Object>()
            {
               @Override
               public Object call() throws Exception
               {
                  repository = (MutableAddonRepository) runnable.furnace
                           .addRepository(AddonRepositoryMode.MUTABLE, addonDir);
                  furnace.setServerMode(true);
                  furnace.start(loader);
                  return furnace;
               }
            });
         }
         catch (Exception e)
         {
            throw new RuntimeException("Failed to start Furnace container.", e);
         }
      }

      public void stop()
      {
         furnace.stop();
         Thread.currentThread().interrupt();
      }

   }
}

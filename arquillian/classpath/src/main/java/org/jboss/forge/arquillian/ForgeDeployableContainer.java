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
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.ForgeImpl;
import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.container.repositories.MutableAddonRepository;
import org.jboss.forge.container.util.Addons;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.forge.container.util.Files;
import org.jboss.forge.maven.dependencies.FileResourceFactory;
import org.jboss.forge.maven.dependencies.MavenContainer;
import org.jboss.forge.maven.dependencies.MavenDependencyResolver;
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

      if (archive instanceof ForgeArchive)
      {
         ShrinkWrapUtil.toFile(new File(destDir.getAbsolutePath() + "/" + archive.getName()), archive);
         ShrinkWrapUtil.unzip(destDir, archive);

         repository.deploy(addonToDeploy, ((ForgeArchive) archive).getAddonDependencies(), new ArrayList<File>());
         repository.enable(addonToDeploy);
      }
      else if (archive instanceof ForgeRemoteAddon)
      {
         ForgeRemoteAddon remoteAddon = (ForgeRemoteAddon) archive;
         AddonManager addonManager = new AddonManagerImpl(runnable.forge, new MavenDependencyResolver(
                  new FileResourceFactory(), new MavenContainer()));
         InstallRequest request = addonManager.install(remoteAddon.getAddonId());
         request.perform();
      }
      else
      {
         throw new IllegalArgumentException(
                  "Invalid Archive type. Ensure that your @Deployment method returns type 'ForgeArchive'.");
      }

      AddonRegistry registry = runnable.getForge().getAddonRegistry();

      try
      {
         Future<Void> future = registry.start(addonToDeploy);
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
         this.addonDir = File.createTempFile("forge", "test-addon-dir");
         runnable = new ForgeRunnable(addonDir, ClassLoader.getSystemClassLoader());
         thread = new Thread(runnable, "Arquillian Forge Runtime");
         System.out.println("Executing test case with addon dir [" + addonDir + "]");
         this.repository = AddonRepositoryImpl.forDirectory(runnable.forge, addonDir);

         thread.start();
      }
      catch (IOException e)
      {
         throw new LifecycleException("Failed to create temporary addon directory", e);
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
      Files.delete(addonDir, true);
   }

   @Override
   public void undeploy(Archive<?> archive) throws DeploymentException
   {
      AddonId addonToUndeploy = getAddonEntry(deploymentInstance.get());
      AddonRegistry registry = runnable.getForge().getAddonRegistry();

      try
      {
         repository.disable(addonToUndeploy);
         Addon addonToStop = registry.getAddon(addonToUndeploy);
         registry.stop(addonToStop);
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
      throw new UnsupportedOperationException("Descriptors not supported by Forge");
   }

   private class ForgeRunnable implements Runnable
   {
      private Forge forge;
      private ClassLoader loader;
      private File addonDir;

      public ForgeRunnable(File addonDir, ClassLoader loader)
      {
         this.forge = new ForgeImpl();
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
               forge.setServerMode(true)
                        .addRepository(AddonRepositoryMode.MUTABLE, addonDir)
                        .start(loader);
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

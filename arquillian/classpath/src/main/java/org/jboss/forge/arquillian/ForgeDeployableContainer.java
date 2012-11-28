package org.jboss.forge.arquillian;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.client.protocol.metadata.Servlet;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.arquillian.protocol.ServletProtocolDescription;
import org.jboss.forge.arquillian.util.ShrinkWrapUtil;
import org.jboss.forge.container.Addon;
import org.jboss.forge.container.AddonEntry;
import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.Status;
import org.jboss.forge.container.impl.AddonRepositoryImpl;
import org.jboss.forge.container.util.ClassLoaders;
import org.jboss.forge.container.util.Files;
import org.jboss.forge.container.util.Streams;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public class ForgeDeployableContainer implements DeployableContainer<ForgeContainerConfiguration>
{
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
   private static final int TEST_DEPLOYMENT_TIMEOUT = 60000;
   private ForgeRunnable thread;
   private File addonDir;

   private AddonRepository repository;

   private Map<Archive<?>, AddonEntry> deployedAddons = new HashMap<Archive<?>, AddonEntry>();

   @Override
   public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException
   {
      AddonEntry addon = getAddonEntry(archive);
      File destDir = repository.getAddonBaseDir(addon);
      destDir.mkdirs();

      if (!(archive instanceof ForgeArchive))
         throw new IllegalArgumentException(
                  "Invalid Archive type. Ensure that your @Deployment method returns type 'ForgeArchive'.");

      ShrinkWrapUtil.toFile(new File(destDir.getAbsolutePath() + "/" + archive.getName()), archive);
      ShrinkWrapUtil.unzip(destDir, archive);

      Node node = archive.get("/META-INF/forge.xml");
      if (node != null)
      {
         Asset asset = node.getAsset();
         try
         {
            Streams.write(asset.openStream(), new FileOutputStream(repository.getAddonDescriptor(addon)));
         }
         catch (FileNotFoundException e)
         {
            throw new DeploymentException("Could not open addon descriptor [" + repository.getAddonDescriptor(addon)
                     + "].", e);
         }
      }

      repository.enable(addon);

      HTTPContext httpContext = new HTTPContext("localhost", 4141);
      httpContext.add(new Servlet("ArquillianServletRunner", "/ArquillianServletRunner"));

      long start = System.currentTimeMillis();
      boolean deployed = false;
      while (!deployed && (System.currentTimeMillis() - start < TEST_DEPLOYMENT_TIMEOUT))
      {
         AddonRegistry registry = thread.getForge().getAddonRegistry();
         for (Addon entry : registry.getRegisteredAddons())
         {
            if (entry.getId().equals(addon.toModuleId()))
            {
               if (Status.STARTED.equals(entry.getStatus()) || Status.FAILED.equals(entry.getStatus()))
                  deployed = true;

               break;
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

      return new ProtocolMetaData()
               .addContext(httpContext);
   }

   @Override
   public void deploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Forge");
   }

   private AddonEntry getAddonEntry(Archive<?> archive)
   {
      if (!deployedAddons.containsKey(archive))
      {
         AddonEntry entry = AddonEntry.from(archive.getName().replaceFirst("\\.jar$", ""),
                  UUID.randomUUID().toString(),
                  thread.getForge().getVersion()
                  );
         deployedAddons.put(archive, entry);
      }
      return deployedAddons.get(archive);
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
         thread = new ForgeRunnable(addonDir, ClassLoader.getSystemClassLoader());
         new Thread(thread).start();
      }
      catch (Exception e)
      {
         throw new LifecycleException("Could not start Forge thread.", e);
      }
   }

   @Override
   public void stop() throws LifecycleException
   {
      this.thread.stop();
   }

   @Override
   public void undeploy(Archive<?> archive) throws DeploymentException
   {
      AddonEntry addon = getAddonEntry(archive);
      repository.disable(addon);

      long start = System.currentTimeMillis();
      boolean deployed = true;
      while (deployed && (System.currentTimeMillis() - start < TEST_DEPLOYMENT_TIMEOUT))
      {
         deployed = false;
         AddonRegistry registry = thread.getForge().getAddonRegistry();
         for (Addon entry : registry.getRegisteredAddons())
         {
            if (entry.getId().equals(addon.toModuleId()))
            {
               deployed = true;
               break;
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

      File dir = repository.getAddonBaseDir(addon);
      boolean deleted = Files.delete(dir, true);
      if (!deleted)
         throw new IllegalStateException("Could not delete file [" + dir.getAbsolutePath() + "]");
   }

   @Override
   public void undeploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Forge");
   }

}

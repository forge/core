package org.jboss.forge.arquillian;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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
import org.jboss.forge.container.Status;
import org.jboss.forge.container.impl.AddonEntry;
import org.jboss.forge.container.impl.AddonRegistry;
import org.jboss.forge.container.impl.AddonUtil;
import org.jboss.forge.container.impl.Bootstrap;
import org.jboss.forge.container.impl.Forge;
import org.jboss.forge.container.impl.util.Files;
import org.jboss.forge.container.impl.util.Streams;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public class ForgeDeployableContainer implements DeployableContainer<ForgeContainerConfiguration>
{
   private ForgeRunnable thread;

   private class ForgeRunnable implements Runnable
   {
      private Forge forge;

      @Override
      public void run()
      {
         forge = Bootstrap.init();
         forge.start();
      }

      public void stop()
      {
         forge.stop();
         Thread.currentThread().interrupt();
      }

      public Forge getForge()
      {
         return forge;
      }

   }

   @Override
   public Class<ForgeContainerConfiguration> getConfigurationClass()
   {
      return ForgeContainerConfiguration.class;
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
         thread = new ForgeRunnable();
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
   public ProtocolDescription getDefaultProtocol()
   {
      return new ServletProtocolDescription();
   }

   @Override
   public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException
   {
      AddonEntry addon = getAddonEntry(archive);
      File destDir = AddonUtil.getAddonSlotDir(addon);
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
            Streams.write(asset.openStream(), new FileOutputStream(AddonUtil.getAddonDescriptor(addon)));
         }
         catch (FileNotFoundException e)
         {
            throw new DeploymentException("Could not open addon descriptor [" + AddonUtil.getAddonDescriptor(addon)
                     + "].", e);
         }
      }

      addon = AddonUtil.install(addon);

      HTTPContext httpContext = new HTTPContext("localhost", 4141);
      httpContext.add(new Servlet("ArquillianServletRunner", "/ArquillianServletRunner"));

      boolean deployed = false;
      while (!deployed)
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
   public void undeploy(Archive<?> archive) throws DeploymentException
   {
      AddonEntry addon = getAddonEntry(archive);
      AddonUtil.remove(addon);

      File dir = AddonUtil.getAddonBaseDir(addon);
      boolean deleted = Files.delete(dir, true);
      if (!deleted)
         throw new IllegalStateException("Could not delete file [" + dir.getAbsolutePath() + "]");
   }

   private AddonEntry getAddonEntry(Archive<?> archive)
   {
      return new AddonEntry(archive.getName().replaceFirst("\\.jar$", ""), "2.0.0-SNAPSHOT", "main");
   }

   @Override
   public void deploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Forge");
   }

   @Override
   public void undeploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Descriptors not supported by Forge");
   }

}

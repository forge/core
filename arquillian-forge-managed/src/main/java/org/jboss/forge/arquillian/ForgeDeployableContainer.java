package org.jboss.forge.arquillian;

import java.io.File;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.forge.arquillian.util.ShrinkWrapUtil;
import org.jboss.forge.container.AddonUtil;
import org.jboss.forge.container.AddonUtil.AddonEntry;
import org.jboss.forge.container.util.Files;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public class ForgeDeployableContainer implements DeployableContainer<ForgeContainerConfiguration>
{
   private AddonEntry addon;
   private File destDir;

   @Override
   public Class<ForgeContainerConfiguration> getConfigurationClass()
   {
      return ForgeContainerConfiguration.class;
   }

   @Override
   public void setup(ForgeContainerConfiguration configuration)
   {
      // TODO Auto-generated method stub
   }

   @Override
   public void start() throws LifecycleException
   {
   }

   @Override
   public void stop() throws LifecycleException
   {
   }

   @Override
   public ProtocolDescription getDefaultProtocol()
   {
      return ProtocolDescription.DEFAULT;
   }

   @Override
   public ProtocolMetaData deploy(Archive<?> archive) throws DeploymentException
   {
      addon = AddonUtil.install(archive.getName(), "2.0.0-SNAPSHOT", "main");
      destDir = AddonUtil.getAddonDirectory(addon);
      destDir.mkdirs();

      File jar = new File(destDir.getAbsolutePath() + "/plugin.jar");

      ShrinkWrapUtil.toFile(jar, archive);

      try
      {
         int status = NativeSystemCall.exec("java", "-Dforge.home=/Users/lbaxter/dev/forge",
                  "-jar", "/Users/lbaxter/dev/forge/jboss-modules.jar", "-modulepath",
                  "/Users/lbaxter/dev/forge/modules:/Users/lbaxter/.forge/plugins:", "org.jboss.forge");

         System.out.println("Forge process exited with status code: " + status);
      }
      catch (Exception e)
      {
         throw new DeploymentException("Could not start Forge process.", e);
      }

      return new ProtocolMetaData();
   }

   @Override
   public void undeploy(Archive<?> archive) throws DeploymentException
   {
      AddonUtil.remove(addon);

      File dir = AddonUtil.getAddonDirectory(addon);
      boolean deleted = Files.delete(dir, true);
      if (!deleted)
         throw new IllegalStateException("Could not delete file [" + dir.getAbsolutePath() + "]");
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

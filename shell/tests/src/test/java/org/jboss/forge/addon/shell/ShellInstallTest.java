package org.jboss.forge.addon.shell;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.FurnaceImpl;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.maven.addon.dependencies.FileResourceFactory;
import org.jboss.forge.maven.addon.dependencies.MavenContainer;
import org.jboss.forge.maven.addon.dependencies.MavenDependencyResolver;
import org.junit.Test;

public class ShellInstallTest
{
   @Test
   public void test()
   {
      Furnace forge = new FurnaceImpl();
      AddonManager addonManager = new AddonManagerImpl(forge, new MavenDependencyResolver(
               new FileResourceFactory(), new MavenContainer()));
      InstallRequest request = addonManager.install(AddonId.from("org.jboss.forge.addon:shell", "2.0.0-SNAPSHOT"));
      request.perform();
   }
}

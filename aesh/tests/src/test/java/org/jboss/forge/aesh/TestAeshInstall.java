package org.jboss.forge.aesh;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.ForgeImpl;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.maven.dependencies.FileResourceFactory;
import org.jboss.forge.maven.dependencies.MavenContainer;
import org.jboss.forge.maven.dependencies.MavenDependencyResolver;
import org.junit.Test;

public class TestAeshInstall
{
   @Test
   public void test()
   {
      Forge forge = new ForgeImpl();
      AddonManager addonManager = new AddonManagerImpl(forge, new MavenDependencyResolver(
               new FileResourceFactory(), new MavenContainer()));
      InstallRequest request = addonManager.install(AddonId.from("org.jboss.forge.addon:aesh", "2.0.0-SNAPSHOT"));
      request.perform();
   }
}

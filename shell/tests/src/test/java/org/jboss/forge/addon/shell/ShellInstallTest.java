/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
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

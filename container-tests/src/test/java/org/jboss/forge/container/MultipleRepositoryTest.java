/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.io.File;
import java.io.IOException;

import org.example.LifecycleListenerService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.impl.AddonManagerImpl;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.maven.dependencies.FileResourceFactory;
import org.jboss.forge.maven.dependencies.MavenContainer;
import org.jboss.forge.maven.dependencies.MavenDependencyResolver;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Ignore
@RunWith(Arquillian.class)
public class MultipleRepositoryTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap.create(ForgeArchive.class).addBeansXML();
   }

   @Deployment(name = "deployed,1")
   public static ForgeArchive getDeployed1()
   {
      return ShrinkWrap.create(ForgeArchive.class).addBeansXML();
   }

   @Deployment(name = "deployed,2")
   public static ForgeArchive getDeployed2()
   {
      return ShrinkWrap.create(ForgeArchive.class).addBeansXML().addClass(LifecycleListenerService.class);
   }

   @Test
   public void testInstallIntoMultipleRepositoriesDefaultsToFirst() throws IOException
   {
      Forge forge = new ForgeImpl();
      File repodir1 = File.createTempFile("forge", "repo1");
      File repodir2 = File.createTempFile("forge", "repo2");
      forge.addRepository(AddonRepositoryMode.MUTABLE, repodir1);
      forge.addRepository(AddonRepositoryMode.MUTABLE, repodir2);
      forge.startAsync();

      AddonManager manager = new AddonManagerImpl(forge, new MavenDependencyResolver(new FileResourceFactory(),
               new MavenContainer()));

      manager.install(AddonId.from("org.jboss", null));
   }
}

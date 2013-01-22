/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.convert;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.environment.Environment;
import org.jboss.forge.environment.impl.EnvironmentImpl;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CDIEnvironmentTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsLibraries(
                        Maven.resolver().offline().loadPomFromFile("pom.xml").importRuntimeDependencies().asFile())
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("org.jboss.forge:environment", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Deployment(testable = false, name = "org.jboss.forge:environment,2.0.0-SNAPSHOT")
   public static ForgeArchive getUIDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addPackages(true, EnvironmentImpl.class.getPackage())
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Inject
   private Environment environment;

   @Test
   public void testNotNull() throws Exception
   {
      Assert.assertNotNull(environment);
   }

}

/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.projects;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingResult;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class MavenBuildManagerTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static Archive<?> getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class)
               .addAsServiceProvider(Service.class, MavenBuildManagerTest.class)
               .addAsResource(MavenBuildManager.class.getResource("pom.xml"), "pom.xml");
   }

   @Before
   public void setUp() throws Exception
   {
      ClassLoader classLoader = getClass().getClassLoader();
      this.resourceFactory = SimpleContainer.getServices(classLoader, ResourceFactory.class).get();
      URL url = classLoader.getResource("pom.xml");
      pomFile = new File(Files.createTempDirectory("junit").toFile(), "pom.xml");
      try (InputStream source = url.openStream();
               FileOutputStream dest = new FileOutputStream(pomFile))
      {
         Streams.write(source, dest);
      }
   }

   private ResourceFactory resourceFactory;
   private File pomFile;

   /**
    * Test method for
    * {@link org.jboss.forge.addon.maven.projects.MavenBuildManager#getModelBuildingResult(org.jboss.forge.addon.maven.resources.MavenModelResource)}.
    */
   @Test
   public void testEffetiveModelProperty() throws Exception
   {
      MavenModelResource model = resourceFactory.create(pomFile).reify(MavenModelResource.class);
      MavenBuildManager manager = new MavenBuildManager();
      ModelBuildingResult modelBuildingResult = manager.getModelBuildingResult(model);
      Model effectiveModel = modelBuildingResult.getEffectiveModel();
      Assert.assertEquals("2.4.3", effectiveModel.getProperties().getProperty("version.shade.plugin"));
   }
}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import org.apache.maven.model.Model;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.resources.MavenModelResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.container.simple.Service;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.forge.parser.xml.Node;
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
public class MavenFacetTest
{

   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:resources"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:simple")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addAsResource(Paths.get("src/test/resources/pom-template.xml").toFile(), "templates/pom-template.xml")
               .addAsServiceProvider(Service.class, MavenFacetTest.class);

      return archive;
   }

   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }

   @Test
   public void testSortedProperties() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Assert.assertTrue(project.hasFacet(MavenFacet.class));
      MavenFacet facet = project.getFacet(MavenFacet.class);
      Model model = facet.getModel();
      model.addProperty("A", "1");
      model.addProperty("B", "2");
      model.addProperty("C", "3");
      model.addProperty("D", "4");
      model.addProperty("E", "5");
      facet.setModel(model);

      MavenModelResource modelResource = facet.getModelResource();
      Node contents = modelResource.getXmlSource();
      Node propertiesNode = contents.get("properties").get(0);
      List<Node> propEntries = propertiesNode.getChildren();
      Assert.assertEquals(5, propEntries.size());
      Assert.assertEquals("A", propEntries.get(0).getName());
      Assert.assertEquals("B", propEntries.get(1).getName());
      Assert.assertEquals("C", propEntries.get(2).getName());
      Assert.assertEquals("D", propEntries.get(3).getName());
      Assert.assertEquals("E", propEntries.get(4).getName());
   }

   @Test
   public void testPreservePOMFormat() throws Exception
   {
      String pom;
      try (InputStream is = getClass().getClassLoader().getResourceAsStream("templates/pom-template.xml"))
      {
         pom = Streams.toString(is);
      }
      Project project = projectFactory.createTempProject();
      MavenFacet facet = project.getFacet(MavenFacet.class);
      MavenModelResource modelResource = facet.getModelResource();
      modelResource.setContents(pom);
      Model model = modelResource.getCurrentModel();
      model.setArtifactId("mytest");
      facet.setModel(model);
      Assert.assertEquals(pom.replace("myartifactid", "mytest").trim(), modelResource.getContents().trim());
   }
}

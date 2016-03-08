/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.resources;

import java.util.Arrays;

import org.apache.maven.model.Model;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class MavenModelResourceTest
{
   private Project project;
   private ProjectFactory projectFactory;

   @Before
   public void setUp()
   {
      projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
      project = projectFactory
               .createTempProject(Arrays.<Class<? extends ProjectFacet>> asList(WebResourcesFacet.class));
   }

   @Test
   public void testGetCurrentModelCallsShouldReturnDifferentObjects()
   {
      MavenModelResource modelResource = project.getRoot().getChild("pom.xml").reify(MavenModelResource.class);
      Model currentModel = modelResource.getCurrentModel();
      currentModel.setInceptionYear("2016");
      Assert.assertNull(modelResource.getCurrentModel().getInceptionYear());
      Assert.assertNotSame(currentModel, modelResource.getCurrentModel());
   }

}

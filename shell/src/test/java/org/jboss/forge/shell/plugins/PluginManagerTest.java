/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class PluginManagerTest extends AbstractShellTest
{
   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private PluginManager pluginManager;

   private static String getAbsolutePath(String path) throws FileNotFoundException
   {
      URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
      if (resource == null)
         throw new FileNotFoundException(path);
      return resource.getFile();
   }

   @Test
   public void testFindProjectSingleNoCoordinates() throws Exception
   {
      File singleProjectRoot = new File(getAbsolutePath("plugins/single"));
      Assert.assertTrue(singleProjectRoot.isDirectory());
      DirectoryResource projectResource = resourceFactory.getResourceFrom(singleProjectRoot).reify(
               DirectoryResource.class);
      Assert.assertTrue(projectResource.isDirectory());
      Project project = projectFactory.findProject(projectResource);
      Assert.assertNotNull("Project not found", project);
      Project pluginProject = pluginManager.findPluginProject(project, null);
      Assert.assertEquals(project, pluginProject);
   }

   @Test
   public void testFindMultipleProjectNoCoordinates() throws Exception
   {
      File singleProjectRoot = new File(getAbsolutePath("plugins/multiple"));
      Assert.assertTrue(singleProjectRoot.isDirectory());
      DirectoryResource projectResource = resourceFactory.getResourceFrom(singleProjectRoot).reify(
               DirectoryResource.class);
      Assert.assertTrue(projectResource.isDirectory());
      Project project = projectFactory.findProject(projectResource);
      Assert.assertNotNull("Project not found", project);
      Project pluginProject = pluginManager.findPluginProject(project, null);
      Assert.assertNotNull("Plugin project not found", pluginProject);
      Assert.assertFalse(project.equals(pluginProject));
      MetadataFacet facet = pluginProject.getFacet(MetadataFacet.class);
      Assert.assertEquals("module_a", facet.getProjectName());
   }

   @Test
   public void testFindMultipleProjectWithCoordinates() throws Exception
   {
      File singleProjectRoot = new File(getAbsolutePath("plugins/multiple"));
      Dependency dependency = DependencyBuilder.create().setGroupId("multiple").setArtifactId("module_b_1");
      Assert.assertTrue(singleProjectRoot.isDirectory());
      DirectoryResource projectResource = resourceFactory.getResourceFrom(singleProjectRoot).reify(
               DirectoryResource.class);
      Assert.assertTrue(projectResource.isDirectory());
      Project project = projectFactory.findProject(projectResource);
      Assert.assertNotNull("Project not found", project);
      Project pluginProject = pluginManager.findPluginProject(project, dependency);
      Assert.assertNotNull("Plugin project not found", pluginProject);
      Assert.assertFalse(project.equals(pluginProject));
      MetadataFacet facet = pluginProject.getFacet(MetadataFacet.class);
      Assert.assertEquals("module_b_1", facet.getProjectName());
   }

}

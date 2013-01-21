/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.spec.javaee.FacesAPIFacet;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.jsf.FacesAPIFacetImpl;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class FacesFacetTest extends SingletonAbstractShellTest
{
   @Test
   public void testFacesConfigCreatedWhenInstalled() throws Exception
   {
      Project project = setUpJSF();
      FileResource<?> config = project.getFacet(FacesFacet.class).getConfigFile();

      Assert.assertNotNull(config);
      Assert.assertTrue(config.exists());

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      DirectoryResource child = web.getWebRootDirectory().getOrCreateChildDirectory("views")
               .getOrCreateChildDirectory("test");

      FileResource<?> view = (FileResource<?>) child.getChild("view.xhtml");
      view.createNewFile();

      FacesFacet faces = project.getFacet(FacesFacet.class);
      List<String> webPaths = faces.getWebPaths(view);

      Assert.assertEquals(2, webPaths.size());
      Assert.assertEquals("/views/test/view.jsf", webPaths.get(0));
      Assert.assertEquals("/faces/views/test/view.xhtml", webPaths.get(1));
   }

   @Test
   public void testFacesFacetConvertsFromResourceToWebPathRoundTrip() throws Exception
   {
      Project project = setUpJSF();

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      DirectoryResource child = web.getWebRootDirectory().getOrCreateChildDirectory("views")
               .getOrCreateChildDirectory("test");

      FileResource<?> view = (FileResource<?>) child.getChild("view.xhtml");
      view.createNewFile();

      FacesFacet faces = project.getFacet(FacesFacet.class);
      List<String> webPaths = faces.getWebPaths(view);

      Assert.assertEquals(2, webPaths.size());
      Assert.assertEquals("/views/test/view.jsf", webPaths.get(0));
      Assert.assertEquals("/faces/views/test/view.xhtml", webPaths.get(1));

      Assert.assertEquals(view, faces.getResourceForWebPath(webPaths.get(0)));
      Assert.assertEquals(view, faces.getResourceForWebPath(webPaths.get(1)));
   }

   @Test
   public void testSetupJSFAddsJSFApi() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("", "", "");
      getShell().execute("project install-facet forge.spec.servlet");

      getShell().execute("project install-facet forge.spec.jsf");
      Assert.assertTrue(project.hasFacet(FacesFacet.class));
      Assert.assertFalse(project.hasFacet(FacesAPIFacet.class));

      Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               FacesAPIFacetImpl.JAVAEE6_FACES_21));
      Assert.assertFalse(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               FacesAPIFacetImpl.JAVAEE6_FACES));

      queueInputLines("", "", "");
      getShell().execute("project install-facet forge.spec.jsf.api");
      assertTrue(project.hasFacet(FacesAPIFacet.class));

      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               FacesAPIFacetImpl.JAVAEE6_FACES_21));
      Assert.assertEquals(ScopeType.PROVIDED, project.getFacet(DependencyFacet.class).getEffectiveDependency(
               FacesAPIFacetImpl.JAVAEE6_FACES_21).getScopeTypeEnum());
   }

   private Project setUpJSF() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("", "", "");
      getShell().execute("project install-facet forge.spec.jsf.api");
      Assert.assertTrue(project.hasFacet(FacesFacet.class));

      Assert.assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               FacesAPIFacetImpl.JAVAEE6_FACES_21));
      Assert.assertEquals(ScopeType.PROVIDED, project.getFacet(DependencyFacet.class).getEffectiveDependency(
               FacesAPIFacetImpl.JAVAEE6_FACES_21).getScopeTypeEnum());
      return project;
   }
}

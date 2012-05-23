/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.spec;

import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.Assert;

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
import org.jboss.forge.spec.javaee.jsf.FacesFacetImpl;
import org.jboss.forge.test.SingletonAbstractShellTest;
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

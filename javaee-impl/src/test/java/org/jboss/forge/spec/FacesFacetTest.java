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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.spec.javaee.FacesFacet;
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

      assertNotNull(config);
      assertTrue(config.exists());

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      DirectoryResource child = web.getWebRootDirectory().getOrCreateChildDirectory("views")
               .getOrCreateChildDirectory("test");

      FileResource<?> view = (FileResource<?>) child.getChild("view.xhtml");
      view.createNewFile();

      FacesFacet faces = project.getFacet(FacesFacet.class);
      List<String> webPaths = faces.getWebPaths(view);

      assertEquals(2, webPaths.size());
      assertEquals("/views/test/view.jsf", webPaths.get(0));
      assertEquals("/faces/views/test/view.xhtml", webPaths.get(1));
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

      assertEquals(2, webPaths.size());
      assertEquals("/views/test/view.jsf", webPaths.get(0));
      assertEquals("/faces/views/test/view.xhtml", webPaths.get(1));

      assertEquals(view, faces.getResourceForWebPath(webPaths.get(0)));
      assertEquals(view, faces.getResourceForWebPath(webPaths.get(1)));
   }

   private Project setUpJSF() throws IOException
   {
      Project project = initializeJavaProject();
      queueInputLines("", "", "");
      getShell().execute("project install-facet forge.spec.jsf");
      assertTrue(project.hasFacet(FacesFacet.class));
      return project;
   }
}

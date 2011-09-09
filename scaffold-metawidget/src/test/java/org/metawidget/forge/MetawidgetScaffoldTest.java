package org.metawidget.forge;

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

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.jboss.seam.render.RenderRoot;
import org.jboss.seam.solder.SolderRoot;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class MetawidgetScaffoldTest extends AbstractShellTest
{

   @Deployment
   public static JavaArchive getDeployment()
   {
      JavaArchive archive = SingletonAbstractShellTest.getDeployment()
               .addPackages(true, RenderRoot.class.getPackage())
               .addPackages(true, SolderRoot.class.getPackage())
               .addPackages(true, MetawidgetScaffold.class.getPackage());

      return archive;
   }

   @Test
   public void testScaffoldSetup() throws Exception
   {
      Project project = setupScaffoldProject();
      ServletFacet servlet = project.getFacet(ServletFacet.class);

      Assert.assertTrue(project.hasFacet(MetawidgetScaffold.class));

      Node root = XMLParser.parse(servlet.getConfigFile().getResourceInputStream());
      List<Node> errorPages = root.get("error-page");
      Assert.assertEquals("/404.jsf", errorPages.get(0).getSingle("location").getText());
      Assert.assertEquals("/500.jsf", errorPages.get(1).getSingle("location").getText());

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      FileResource<?> e404 = web.getWebResource("404.xhtml");
      Assert.assertTrue(Streams.toString(e404.getResourceInputStream()).contains(
               "/resources/scaffold/forge-template.xhtml"));

      Assert.assertTrue(web.getWebResource("/resources/scaffold/forge-template.xhtml").exists());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGenerateFromEntity() throws Exception
   {
      Project project = setupScaffoldProject();
      getShell().execute("entity --named Customer");

      queueInputLines("", "");
      getShell().execute("scaffold from-entity");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");

      for (FileResource<?> file : Arrays.asList(view, create, list))
      {
         Assert.assertTrue(file.exists());
         Assert.assertTrue(Streams.toString(file.getResourceInputStream()).contains(
                  "template=\"/resources/scaffold/forge-template.xhtml"));
      }
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGenerateFromEntityWithTemplate() throws Exception
   {
      Project project = setupScaffoldProject();
      getShell().execute("entity --named Customer");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      web.createWebResource("<ui:insert name=\"main\">", "test-template.xhtml");

      queueInputLines("", "");
      getShell().execute(
               "scaffold from-entity --usingTemplate "
                        + web.getWebResource("test-template.xhtml").getFullyQualifiedName());

      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");

      for (FileResource<?> file : Arrays.asList(view, create, list))
      {
         Assert.assertTrue(file.exists());
         Assert.assertTrue(Streams.toString(file.getResourceInputStream()).contains(
                  "template=\"/test-template.xhtml"));
      }
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGenerateFromEntityWithUnsupportedTemplate() throws Exception
   {
      Project project = setupScaffoldProject();
      getShell().execute("entity --named Customer");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);
      web.createWebResource("<ui:insert name=\"other\">", "test-template.xhtml");

      queueInputLines("", "");

      try {

         getShell().execute(
                  "scaffold from-entity --usingTemplate "
                           + web.getWebResource("test-template.xhtml").getFullyQualifiedName());
         fail();
      }
      catch (IllegalStateException e) {}

      FileResource<?> view = web.getWebResource("scaffold/customer/view.xhtml");
      FileResource<?> create = web.getWebResource("scaffold/customer/create.xhtml");
      FileResource<?> list = web.getWebResource("scaffold/customer/list.xhtml");

      for (FileResource<?> file : Arrays.asList(view, create, list))
      {
         Assert.assertFalse(file.exists());
      }
   }

   public Project setupScaffoldProject() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("", "", "", "", "", "");
      getShell().execute("scaffold setup");
      return project;
   }
}

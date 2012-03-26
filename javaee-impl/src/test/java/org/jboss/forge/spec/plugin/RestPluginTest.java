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

package org.jboss.forge.spec.plugin;

import org.apache.log4j.pattern.RelativeTimePatternConverter;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.RestApplicationFacet;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.forge.spec.javaee.RestWebXmlFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.spec.javaee.jpa.PersistenceFacetImpl;
import org.jboss.forge.spec.javaee.rest.RestWebXmlFacetImpl;
import org.jboss.forge.spec.jpa.AbstractJPATest;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.impl.spec.servlet.web.WebAppDescriptorImpl;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class RestPluginTest extends AbstractJPATest
{
   private static final DependencyBuilder JAX_RS_DEPENDENCY = DependencyBuilder
            .create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec");

   @Test
   public void testInstall() throws Exception
   {
      Project project = getProject();

      assertFalse(project.hasFacet(RestFacet.class));
      setupRest();

      assertTrue(project.hasFacet(RestFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec")));

      RestFacet restFacet = project.getFacet(RestFacet.class);
      assertEquals("/rest", restFacet.getApplicationPath());

      assertTrue(project.hasFacet(RestWebXmlFacet.class));
      RestWebXmlFacet restWebXmlFacet = project.getFacet(RestWebXmlFacet.class);
      assertEquals("/rest/*", restWebXmlFacet.getServletPath());
   }

   @Test
   public void testInstallAfterOtherServletMapping() throws Exception
   {
      Project project = getProject();

      assertFalse(project.hasFacet(RestFacet.class));

      queueInputLines("");
      getShell().execute("setup servlet");
      ServletFacet web = project.getFacet(ServletFacet.class);
      WebAppDescriptor config = web.getConfig();
      config.facesServlet();
      web.saveConfig(config);

      org.jboss.forge.parser.xml.Node webXml = XMLParser.parse(web.getConfig().exportAsString());
      assertEquals(1, webXml.get("servlet-mapping").size());

      assertTrue(config.exportAsString().contains("servlet-mapping"));

      setupRest();

      assertTrue(project.hasFacet(RestFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec")));

      RestFacet restFacet = project.getFacet(RestFacet.class);
      assertEquals("/rest", restFacet.getApplicationPath());

      assertTrue(project.hasFacet(RestWebXmlFacet.class));
      RestWebXmlFacet restWebXmlFacet = project.getFacet(RestWebXmlFacet.class);
      assertEquals("/rest/*", restWebXmlFacet.getServletPath());

      webXml = XMLParser.parse(web.getConfig().exportAsString());
      assertEquals(2, webXml.get("servlet-mapping").size());
   }

   @Test
   public void testInstallWithApplicationClass() throws Exception
   {
      Project project = getProject();

      assertFalse(project.hasFacet(RestFacet.class));
      queueInputLines("", "", "demo", "demo", "RestApplication");
      getShell().execute("rest setup --activatorType APP_CLASS");

      assertTrue(project.hasFacet(RestFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               JAX_RS_DEPENDENCY));

      assertTrue(project.hasFacet(RestFacet.class));
      RestApplicationFacet restApplicationFacet = project.getFacet(RestApplicationFacet.class);
      assertNotNull(restApplicationFacet);
   }

   @Test
   public void testCreateEndpoint() throws Exception
   {
      Project project = getProject();
      JavaClass entity = generateEntity(project, null, "User");
      assertFalse(entity.hasAnnotation(XmlRootElement.class));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity");

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.UserEndpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/user", endpoint.getAnnotation(Path.class).getStringValue());
      assertEquals("java.util.List", endpoint.getMethod("listAll").getQualifiedReturnType());
      Method<JavaClass> method = endpoint.getMethod("findById", Long.class);
      Type<JavaClass> returnTypeInspector = method.getReturnTypeInspector();
      assertEquals("com.test." + PersistenceFacetImpl.DEFAULT_ENTITY_PACKAGE + ".User",
               returnTypeInspector
                        .getQualifiedName());

      assertTrue(java.getJavaResource(entity).getJavaSource().hasAnnotation(XmlRootElement.class));
      getShell().execute("build");
   }

   @Test
   public void testCreateEndpointNonStandardId() throws Exception
   {
      Project project = getProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass entity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("User.java"));
      entity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(entity);

      getShell().setCurrentResource(java.getJavaResource(entity));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity");

      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.UserEndpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/user", endpoint.getAnnotation(Path.class).getStringValue());
      assertTrue(endpoint.toString().contains("entity.setObjectId(id);"));
      getShell().execute("build");
   }

   @Test
   public void testCreateEndpointPrimitiveNonStandardId() throws Exception
   {
      Project project = getProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass entity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("User2.java"));
      entity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(entity);

      getShell().setCurrentResource(java.getJavaResource(entity));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity");

      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.User2Endpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/user2", endpoint.getAnnotation(Path.class).getStringValue());
      assertTrue(endpoint.toString().contains("entity.setObjectId(id);"));
      getShell().execute("build");
   }

   @Test
   public void testCreateEndpointPrimitiveNonStandardGetterId() throws Exception
   {
      Project project = getProject();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass entity = JavaParser.parse(JavaClass.class,
               RestPluginTest.class.getResourceAsStream("User3.java"));
      entity.setPackage(java.getBasePackage() + ".model");
      java.saveJavaSource(entity);

      getShell().setCurrentResource(java.getJavaResource(entity));

      setupRest();

      queueInputLines("");
      getShell().execute("rest endpoint-from-entity");

      JavaResource resource = java.getJavaResource(java.getBasePackage() + ".rest.User3Endpoint");
      JavaClass endpoint = (JavaClass) resource.getJavaSource();

      assertEquals("/user3", endpoint.getAnnotation(Path.class).getStringValue());
      assertTrue(endpoint.toString().contains("entity.setObjectId(id);"));
      getShell().execute("build");
   }

   @Test
   public void testInstallWebIntoXML() throws Exception
   {
      Project project = getProject();

      assertFalse(project.hasFacet(RestFacet.class));
      setupRest();

      assertTrue(project.hasFacet(RestFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasEffectiveDependency(
               JAX_RS_DEPENDENCY));

      ServletFacet web = project.getFacet(ServletFacet.class);
      Node servletName = ((WebAppDescriptorImpl) web.getConfig()).getRootNode().getSingle(
               "servlet-mapping/servlet-name=" + RestWebXmlFacetImpl.JAXRS_SERVLET);
      assertNotNull(servletName);
      assertEquals("/rest/*", servletName.getParent().getSingle("url-pattern").getText());
   }

   private void setupRest() throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup rest");
   }
}

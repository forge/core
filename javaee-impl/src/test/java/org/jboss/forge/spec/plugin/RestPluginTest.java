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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.RestActivatorType;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.forge.spec.jpa.AbstractJPATest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class RestPluginTest extends AbstractJPATest
{
   @Test
   public void testInstall() throws Exception
   {
      Project project = getProject();

      assertFalse(project.hasFacet(RestFacet.class));
      setupRest();

      assertTrue(project.hasFacet(RestFacet.class));
      assertTrue(project.getFacet(DependencyFacet.class).hasDependency(BaseJavaEEFacet.dep));

      RestFacet rest = project.getFacet(RestFacet.class);
      assertEquals("/rest/*", rest.getApplicationPath());
      assertEquals(RestActivatorType.WEB_XML, rest.getApplicationActivatorType());
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
      assertEquals("java.util.List", endpoint.getMethod("listAll").getReturnType());
      assertEquals("com.test.domain.User", endpoint.getMethod("findById", long.class).getReturnType());

      assertTrue(java.getJavaResource(entity).getJavaSource().hasAnnotation(XmlRootElement.class));
   }

   private void setupRest() throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup rest");
   }
}

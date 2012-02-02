/**
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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
 **/
package org.jboss.forge.spec.javaee.jsf;

import java.io.InputStream;

import org.jboss.forge.shell.util.Streams;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="http://community.jboss.org/people/bleathem">Brian Leathem</a>
 */
public class ServletMappingHelperTest
{
   @Test
   public void addFacesServletMappingTest() throws Exception
   {
      ServletMappingHelper helper = new ServletMappingHelper();
      InputStream webXml = Streams.fromString(getWebXmlShort());
      InputStream webXml2 = helper.addFacesServletMapping(webXml, "*.jsf");
      String webXml2String = Streams.toString(webXml2);
      Assert.assertNotSame(webXml, webXml2);
      Assert.assertTrue(webXml2String.contains("<url-pattern>*.jsf</url-pattern>"));
      Assert.assertTrue(webXml2String.contains("<servlet-name>Faces Servlet</servlet-name>"));

      webXml2.reset();
      webXml2 = helper.addFacesServletMapping(webXml2, "/faces/*");
      webXml2String = Streams.toString(webXml2);
      Assert.assertNotSame(webXml, webXml2);
      Assert.assertTrue(webXml2String.contains("<url-pattern>/faces/*</url-pattern>"));
      Assert.assertTrue(webXml2String.contains("<servlet-name>Faces Servlet</servlet-name>"));
      Assert.assertTrue(webXml2String.contains("<servlet-mapping>"));

      webXml2.reset();
      InputStream webXml3 = helper.addFacesServletMapping(webXml2, "/faces/*");
      String webXml3String = Streams.toString(webXml3);
      Assert.assertEquals(webXml2String, webXml3String);
   }

   private String getWebXmlShort()
   {
      return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
               +
               "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"3.0\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\">\n"
               +
               "  <display-name>test2</display-name>\n" +
               "  <session-config>\n" +
               "    <session-timeout>30</session-timeout>\n" +
               "  </session-config>\n" +
               "</web-app>";
   }
}

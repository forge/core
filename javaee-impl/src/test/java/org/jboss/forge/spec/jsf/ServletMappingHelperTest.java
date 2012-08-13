/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.jsf;

import java.io.InputStream;

import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.spec.javaee.jsf.FacesFacetImpl.ServletMappingHelper;
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

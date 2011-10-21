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

import org.junit.Assert;
import org.junit.Test;

import javax.faces.webapp.FacesServlet;
import javax.validation.constraints.AssertTrue;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="http://community.jboss.org/people/bleathem">Brian Leathem</a>
 */
public class ServletMappingHelperTest {
    @Test
    public void addFacesServletMappingTest() throws Exception {
        ServletMappingHelper helper = new ServletMappingHelper();
        InputStream webXml = convertStringToInputStream(getWebXmlShort());
        InputStream webXml2 = helper.addFacesServletMapping(webXml, "*.jsf");
        String webXml2String = convertInputStreamToString(webXml2);
        Assert.assertNotSame(webXml, webXml2);
        Assert.assertTrue(webXml2String.contains("<url-pattern>*.jsf</url-pattern>"));
        Assert.assertTrue(webXml2String.contains("<servlet-name>Faces Servlet</servlet-name>"));

        webXml2.reset();
        webXml2 = helper.addFacesServletMapping(webXml2, "/faces/*");
        webXml2String = convertInputStreamToString(webXml2);
        Assert.assertNotSame(webXml, webXml2);
        Assert.assertTrue(webXml2String.contains("<url-pattern>/faces/*</url-pattern>"));
        Assert.assertTrue(webXml2String.contains("<servlet-name>Faces Servlet</servlet-name>"));

        webXml2.reset();
        InputStream webXml3 = helper.addFacesServletMapping(webXml2, "/faces/*");
        String webXml3String = convertInputStreamToString(webXml3);
        Assert.assertEquals(webXml2String, webXml3String);
    }

    private String convertInputStreamToString(InputStream stream) throws Exception {
        stream.reset();
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuilder builder = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
      builder.append(line + "\n");
    }
    stream.close();
    return builder.toString();
  }

    private InputStream convertStringToInputStream(String string) {
        InputStream stream = new ByteArrayInputStream( string.getBytes() );
        return stream;
    }

    private String getWebXml() {
        String webXmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<web-app version=\"3.0\" xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
                " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\">\n" +
                " <display-name>local-demo</display-name>\n" +
                " <context-param>\n" +
                "  <param-name>javax.faces.SKIP_COMMENTS</param-name>\n" +
                "  <param-value>true</param-value>\n" +
                " </context-param>\n" +
                " <!-- \n" +
                " <context-param>\n" +
                "  <param-name>org.jboss.jbossfaces.JSF_CONFIG_NAME</param-name>\n" +
                "  <param-value>MyFaces-2.0</param-value>\n" +
                " </context-param>\n" +
                "  -->\n" +
                " <session-config>\n" +
                "  <session-timeout>30</session-timeout>\n" +
                " </session-config>\n" +
                " <welcome-file-list>\n" +
                "  <welcome-file>/index.html</welcome-file>\n" +
                "  <welcome-file>faces/index.xhtml</welcome-file>\n" +
                " </welcome-file-list>\n" +
                "</web-app>";
        return webXmlString;
    }

    private String getWebXmlShort() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"3.0\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\">\n" +
                "  <display-name>test2</display-name>\n" +
                "  <session-config>\n" +
                "    <session-timeout>30</session-timeout>\n" +
                "  </session-config>\n" +
                "</web-app>";
    }
}

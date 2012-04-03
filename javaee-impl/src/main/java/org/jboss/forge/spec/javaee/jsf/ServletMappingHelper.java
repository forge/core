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
import java.util.List;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;

/**
 * @author <a href="http://community.jboss.org/people/bleathem">Brian Leathem</a>
 */
class ServletMappingHelper
{
   public static final String FACES_SERVLET_CLASS = "javax.faces.webapp.FacesServlet";

   InputStream addFacesServletMapping(final InputStream webXmlStream, final String mapping)
   {
      Node root = XMLParser.parse(webXmlStream);
      Node facesServlet = getOrCreateFacesServlet(root);
      boolean mappingCreated = createMappingIfNotExists(root, facesServlet, mapping);
      if (mappingCreated)
      {
         return XMLParser.toXMLInputStream(root);
      }
      else
      {
         return XMLParser.toXMLInputStream(root);
      }
   }

   Node getOrCreateFacesServlet(final Node root)
   {
      List<Node> servlets = root.get("servlet");
      for (Node servlet : servlets)
      {
         if (FACES_SERVLET_CLASS.equals(servlet.getSingle("servlet-class").getText()))
         {
            return servlet;
         }
      }
      Node servlet = root.createChild("servlet");
      servlet.createChild("servlet-name").text("Faces Servlet");
      servlet.createChild("servlet-class").text(FACES_SERVLET_CLASS);
      servlet.createChild("load-on-startup").text("1");
      return servlet;
   }

   boolean createMappingIfNotExists(final Node root, final Node servlet, final String mapping)
   {
      List<Node> servletMappings = root.get("servlet-mapping");
      Node servletMappingNode = null;
      String servletName = servlet.getSingle("servlet-name").getText();
      for (Node servletMapping : servletMappings)
      {
         if (servletName.equals(servletMapping.getSingle("servlet-name").getText()))
         {
            servletMappingNode = servletMapping;
            List<Node> urlPatterns = servletMapping.get("url-pattern");
            for (Node urlPattern : urlPatterns)
            {
               if (mapping.equals(urlPattern.getText()))
               {
                  return false; // mapping already exists; not created
               }

            }
         }
      }
      // Mapping does not exist, create it and add the url-pattern
      if (servletMappingNode == null)
      {
         servletMappingNode = root.createChild("servlet-mapping");
         servletMappingNode.createChild("servlet-name").text(servletName);
      }
      servletMappingNode.createChild("url-pattern").text(mapping);
      return true;
   }

}

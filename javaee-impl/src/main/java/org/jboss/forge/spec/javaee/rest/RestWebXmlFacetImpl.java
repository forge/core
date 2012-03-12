/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.spec.javaee.rest;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.forge.spec.javaee.RestWebXmlFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.shrinkwrap.descriptor.impl.spec.servlet.web.WebAppDescriptorImpl;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;

import javax.inject.Inject;

/**
 * @Author Paul Bakker - paul.bakker@luminis.eu
 */
@Alias("forge.spec.jaxrs.webxml")
@RequiresFacet(RestFacet.class)
public class RestWebXmlFacetImpl extends BaseFacet implements RestWebXmlFacet
{
   public static final String JAXRS_SERVLET = "javax.ws.rs.core.Application";

   @Inject
   private Configuration config;

   @Override
   public boolean install()
   {
      if (!installedInWebXML())
      {
         // TODO this needs to be fixed in desciptors (allow creation of servlet mapping)
         ServletFacet servlet = project.getFacet(ServletFacet.class);
         WebAppDescriptorImpl web = (WebAppDescriptorImpl) servlet.getConfig();
         Node node = web.getRootNode();
         Node servletClass = node.getSingle("servlet-mapping/servlet-name=" + JAXRS_SERVLET);
         if (servletClass == null)
         {
            Node mapping = node.createChild("servlet-mapping");
            mapping.createChild("servlet-name").text(JAXRS_SERVLET);
            String urlPattern = config.getString(RestFacet.ROOTPATH);
            if (urlPattern.endsWith("/"))
            {
               urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
            }
            mapping.createChild("url-pattern").text(urlPattern + "/*");
         }

         servlet.saveConfig(web);
      }

      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return installedInWebXML();
   }

   private boolean installedInWebXML()
   {
      return getServletPath() != null;
   }

   public String getServletPath()
   {
      ServletFacet servlet = project.getFacet(ServletFacet.class);
      WebAppDescriptorImpl web = (WebAppDescriptorImpl) servlet.getConfig();

      Node node = web.getRootNode();
      Node servletClass = node.getSingle("servlet-mapping/servlet-name=" + JAXRS_SERVLET);
      if (servletClass != null)
      {
         Node url = servletClass.getParent().getSingle("url-pattern");
         if (url != null)
         {
            return url.getText();
         }
      }
      return null;
   }

   @Override
   public void setApplicationPath(final String path)
   {
      config.setProperty(RestFacet.ROOTPATH, path);
      ServletFacet servlet = project.getFacet(ServletFacet.class);
      WebAppDescriptorImpl web = (WebAppDescriptorImpl) servlet.getConfig();

      Node node = web.getRootNode();
      Node servletClass = node.getSingle("servlet-mapping/servlet-name=" + JAXRS_SERVLET);

      if (servletClass != null)
      {
         servletClass.getParent().getOrCreate("url-pattern").text(path);
      }

      servlet.saveConfig(web);
   }
}

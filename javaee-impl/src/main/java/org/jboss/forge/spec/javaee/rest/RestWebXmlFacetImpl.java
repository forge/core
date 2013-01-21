/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.forge.spec.javaee.RestWebXmlFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.shrinkwrap.descriptor.impl.spec.servlet.web.WebAppDescriptorImpl;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;

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

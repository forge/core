/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.rest.config;

import java.util.List;

import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.ServletMappingType;

/**
 * Configures the Rest facet through the web.xml
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RestWebXmlConfigurationStrategy implements RestConfigurationStrategy
{
   public static final String JAXRS_SERVLET = "javax.ws.rs.core.Application";

   private final String path;

   public RestWebXmlConfigurationStrategy(String path)
   {
      Assert.notNull(path, "Path cannot be null");
      this.path = path;
   }

   @Override
   public String getDescription()
   {
      return "Configure in web.xml";
   }

   @Override
   public String getApplicationPath()
   {
      return path;
   }

   @Override
   public void install(Project project)
   {
      if (!installedInWebXML(project))
      {
         ServletFacet_3_0 servlet = project.getFacet(ServletFacet_3_0.class);
         WebAppDescriptor config = servlet.getConfig();

         config.createServlet().servletName(JAXRS_SERVLET).loadOnStartup(1);
         String urlPattern = path;
         if (urlPattern.endsWith("/"))
         {
            urlPattern = urlPattern.substring(0, urlPattern.length() - 1);
         }
         config.createServletMapping().servletName(JAXRS_SERVLET).urlPattern(urlPattern);
         servlet.saveConfig(config);
      }
   }

   private boolean installedInWebXML(Project project)
   {
      return getServletPath(project) != null;
   }

   static String getServletPath(Project project)
   {
      String servletPath = null;
      if (project.hasFacet(ServletFacet_3_0.class))
      {
         ServletFacet_3_0 servlet = project.getFacet(ServletFacet_3_0.class);
         WebAppDescriptor config = servlet.getConfig();
         List<ServletMappingType<WebAppDescriptor>> allServletMapping = config.getAllServletMapping();
         for (ServletMappingType<WebAppDescriptor> servletMappingType : allServletMapping)
         {
            if (JAXRS_SERVLET.equals(servletMappingType.getServletName()))
            {
               List<String> allUrlPattern = servletMappingType.getAllUrlPattern();
               for (String urlPattern : allUrlPattern)
               {
                  servletPath = urlPattern;
               }
            }
         }
      }
      return servletPath;
   }

   @Override
   public void uninstall(Project project)
   {
      // TODO Auto-generated method stub

   }
}
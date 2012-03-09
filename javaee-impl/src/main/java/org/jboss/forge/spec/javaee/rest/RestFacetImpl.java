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
package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.resources.java.JavaResourceVisitor;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.RestActivatorType;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.shrinkwrap.descriptor.impl.spec.servlet.web.WebAppDescriptorImpl;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.jaxrs")
@RequiresFacet(ServletFacet.class)
public class RestFacetImpl extends BaseJavaEEFacet implements RestFacet
{
   private static final String REST_ACTIVATOR_CONFIG_KEY = "org.jboss.forge.spec.javaee.rest.activatorClass";

   public static final String JAXRS_SERVLET = "javax.ws.rs.core.Application";

   private Configuration config;
   private ShellPrompt prompt;

   @Inject
   public RestFacetImpl(final DependencyInstaller installer, Configuration config,
            ShellPrompt prompt)
   {
      super(installer);
      this.config = config;
      this.prompt = prompt;
   }

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
            String urlPattern = prompt.prompt("Serve REST resources under which URL path?", "/rest/*");
            Node mapping = node.createChild("servlet-mapping");
            mapping.createChild("servlet-name").text(JAXRS_SERVLET);
            mapping.createChild("url-pattern").text(urlPattern);
         }

         servlet.saveConfig(web);
      }
      return super.install();
   }

   @Override
   public boolean isInstalled()
   {
      // TODO support additional configuration types (RestActivationProvider)
      return (installedInWebXML() || installedInAnnotatedClass()) && super.isInstalled();
   }

   private boolean installedInAnnotatedClass()
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      JaxRsAnnotationVisitor visitor = new JaxRsAnnotationVisitor();
      String className = config.getString(REST_ACTIVATOR_CONFIG_KEY);
      if (className != null)
      {
         try
         {
            visitor.visit(java.getJavaResource(className));
            if(visitor.isFound())
            {
               config.setProperty(REST_ACTIVATOR_CONFIG_KEY, visitor.getSource().getQualifiedName());
            }
         }
         catch (FileNotFoundException e)
         {
            config.clearProperty(REST_ACTIVATOR_CONFIG_KEY);
         }
      }

      if (!visitor.isFound())
         java.visitJavaSources(visitor);
      
      return visitor.isFound();
   }

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList(
               (Dependency) DependencyBuilder.create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec"));
   }

   private boolean installedInWebXML()
   {
      return getApplicationPath() != null;
   }

   @Override
   public String getApplicationPath()
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

   @Override
   public RestActivatorType getApplicationActivatorType()
   {
      if(installedInWebXML())
      {
         return RestActivatorType.WEB_XML;
      }
      else
      {
         return RestActivatorType.APP_CLASS_AND_ANNOTATION;
      }
   }
}

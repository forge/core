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
package org.jboss.forge.spec.javaee.servlet;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("servlet")
@RequiresProject
public class ServletPlugin implements Plugin
{
   @Inject
   private Project project;

   @Inject
   private Event<InstallFacets> request;

   @DefaultCommand
   public void status(final PipeOut out)
   {
      if (project.hasFacet(ServletFacet.class))
      {
         ShellMessages.success(out, "Servlet is installed.");
      }
      else
      {
         ShellMessages.warn(out, "Servlet is NOT installed.");
      }
   }

   @SetupCommand
   public void setup(final PipeOut out, @Option(name = "quickstart") final boolean quickstart)
   {
      if (!project.hasFacet(ServletFacet.class))
      {
         request.fire(new InstallFacets(ServletFacet.class));
      }
      if (quickstart)
      {
         // webRoot is created by the ServletFacet install
         DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
         installQuickstart(webRoot);
      }
      status(out);
   }

   private void installQuickstart(DirectoryResource webRoot)
   {
      ((FileResource<?>) webRoot.getChild("404.html")).setContents(getClass().getResourceAsStream(
               "/org/jboss/forge/web/404.html"));
      ((FileResource<?>) webRoot.getChild("500.html")).setContents(getClass().getResourceAsStream(
               "/org/jboss/forge/web/500.html"));
      ((FileResource<?>) webRoot.getChild("index.html")).setContents(getClass().getResourceAsStream(
               "/org/jboss/forge/web/index.html"));
      ((FileResource<?>) webRoot.getChild("forge-logo.png")).setContents(getClass().getResourceAsStream(
               "/org/jboss/forge/web/forge-logo.png"));
      ((FileResource<?>) webRoot.getChild("forge-style.css")).setContents(getClass().getResourceAsStream(
               "/org/jboss/forge/web/forge-style.css"));
      ((FileResource<?>) webRoot.getChild("jboss-community.png")).setContents(getClass().getResourceAsStream(
               "/org/jboss/forge/web/jboss-community.png"));
      ((FileResource<?>) webRoot.getChild("bkg.gif")).setContents(getClass().getResourceAsStream(
               "/org/jboss/forge/web/bkg.gif"));
      ((FileResource<?>) webRoot.getChild("favicon.ico")).setContents(getClass().getResourceAsStream(
               "/org/jboss/forge/web/favicon.ico"));

      FileResource<?> descriptor = project.getFacet(ServletFacet.class).getConfigFile();
      WebAppDescriptor unit = Descriptors.create(WebAppDescriptor.class)
               .welcomeFile("/index.html");

      unit.errorPage(404, "/404.html");
      unit.errorPage(500, "/500.html");

      descriptor.setContents(unit.exportAsString());
   }

}

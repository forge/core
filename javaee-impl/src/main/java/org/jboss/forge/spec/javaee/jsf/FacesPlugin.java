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
package org.jboss.forge.spec.javaee.jsf;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.FacesProjectStage;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("faces")
public class FacesPlugin implements Plugin
{
   @Inject
   private Project project;

   @Inject
   private Event<InstallFacets> request;

   @Command("setup")
   public void setup(final PipeOut out)
   {
      if (!project.hasFacet(FacesFacet.class))
      {
         request.fire(new InstallFacets(FacesFacet.class));
      }

      if (project.hasFacet(FacesFacet.class))
      {
         ShellMessages.success(out, "JavaServer Faces is installed.");
      }
   }

   @Command("project-stage")
   public void setProjectStage(@Option(name = "set") final FacesProjectStage stage, final PipeOut out)
   {
      if (!project.hasFacet(FacesFacet.class))
      {
         throw new RuntimeException("JSF is not installed. Use 'setup faces' to continue.");
      }

      ServletFacet srv = project.getFacet(ServletFacet.class);
      WebAppDescriptor config = srv.getConfig();
      if (stage == null)
      {
         ShellMessages.info(out, "Project stage is currently: " + config.getFacesProjectStage().getStage());
      }
      else
      {
         config.facesProjectStage(stage);
         srv.saveConfig(config);
         ShellMessages.success(out, "Faces PROJECT_STAGE updated to: " + stage.getStage());
      }
   }

   @DefaultCommand
   public void show(final PipeOut out)
   {
      if (project.hasFacet(FacesFacet.class))
      {
         FacesFacet facet = project.getFacet(FacesFacet.class);
         ShellMessages.info(out, "Displaying current JSF configuration:");

         out.println();
         out.println(out.renderColor(ShellColor.BOLD, "Project State: ") + facet.getProjectStage());
         out.println(out.renderColor(ShellColor.BOLD, "FacesServlet Mappings: ") + facet.getFacesServletMappings());
         out.println(out.renderColor(ShellColor.BOLD, "Faces Default Suffixes: ") + facet.getFacesDefaultSuffixes());
         out.println(out.renderColor(ShellColor.BOLD, "Facelets Default Suffixes: ")
                  + facet.getFaceletsDefaultSuffixes());
         out.println(out.renderColor(ShellColor.BOLD, "Facelets View Mappings: ") + facet.getFaceletsViewMapping());
      }
      else
      {
         ShellMessages.info(out, "JSF is not installed. Use 'setup faces' to continue.");
      }
   }
}

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
package org.jboss.seam.forge.spec.javaee6.jsf;

import javax.inject.Inject;

import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.shell.ShellMessages;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.PipeOut;
import org.jboss.seam.forge.shell.plugins.Plugin;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.spec.javaee6.servlet.ServletFacet;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.FacesProjectStage;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("faces")
@RequiresFacet(FacesFacet.class)
public class FacesPlugin implements Plugin
{
   @Inject
   private Project project;

   @Command("project-stage")
   public void setProjectStage(@Option(name = "set") FacesProjectStage stage, PipeOut out)
   {
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
}

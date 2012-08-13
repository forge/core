/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jsf;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.FacesAPIFacet;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.FacesProjectStage;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
@Alias("faces")
@RequiresFacet(FacesFacet.class)
@RequiresProject
public class FacesPlugin implements Plugin
{
   @Inject
   private Project project;

   @Inject
   private Event<InstallFacets> request;

   @Inject
   private Instance<TemplateCompiler> compiler;

   @Inject
   private ShellPrompt prompt;

   @SetupCommand
   public void setup(final PipeOut out)
   {
      if (!project.hasFacet(FacesAPIFacet.class))
      {
         request.fire(new InstallFacets(FacesAPIFacet.class));
         if (!project.hasFacet(CDIFacet.class)) {
            if (prompt.promptBoolean("Do you also want to install CDI?", true)) {
               request.fire(new InstallFacets(CDIFacet.class));
            }
         }
      }
      FacesFacet facet = project.getFacet(FacesFacet.class);
      if (facet.getFacesServletMappings().isEmpty())
      {
          if (prompt.promptBoolean("Do you also want to install the Faces servlet and mapping?", false)) {
              facet.setFacesMapping("*.xhtml");
              facet.setFacesMapping("/faces/*");
          }
      }
      
      if (project.hasFacet(FacesFacet.class))
      {
         ShellMessages.success(out, "JavaServer Faces is installed.");
      }
   }

   @Command("project-stage")
   public void setProjectStage(@Option(name = "set") final FacesProjectStage stage, final PipeOut out)
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

   @DefaultCommand
   public void show(final PipeOut out)
   {
      FacesFacet facet = project.getFacet(FacesFacet.class);
      ShellMessages.info(out, "Displaying current JSF configuration:");

      out.println();
      out.println(out.renderColor(ShellColor.BOLD, "Project State: ") + facet.getProjectStage());
      out.println(out.renderColor(ShellColor.BOLD, "FacesServlet Mappings: ") + facet.getEffectiveFacesServletMappings());
      out.println(out.renderColor(ShellColor.BOLD, "Faces Default Suffixes: ") + facet.getFacesDefaultSuffixes());
      out.println(out.renderColor(ShellColor.BOLD, "Facelets Default Suffixes: ")
               + facet.getFaceletsDefaultSuffixes());
      out.println(out.renderColor(ShellColor.BOLD, "Facelets View Mappings: ") + facet.getFaceletsViewMapping());
   }

   private static final String VIEW_TEMPLATE = "org/jboss/forge/web/empty-view.xhtml";

   @Command("new-view")
   public void newView(final PipeOut out, @Option(name = "target") final Resource<?> target)
   {
      CompiledTemplateResource viewTemplate = compiler.get().compile(VIEW_TEMPLATE);
      if (!target.exists())
      {
         ((FileResource<?>) target).createNewFile();
         ((FileResource<?>) target).setContents(viewTemplate.render());
      }
      else if (prompt.promptBoolean("File exists. Overwrite with blank view?"))
      {
         ((FileResource<?>) target).setContents(viewTemplate.render());
      }
      else
      {
         throw new RuntimeException("Aborted. File exists [" + target.getFullyQualifiedName() + "].");
      }
   }
}

package org.jboss.forge.addon.configuration.ui;

import java.io.PrintStream;
import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;


public class ConfigListCommand extends AbstractShellCommand
{
   
   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).category(Categories.create("Configuration")).name("Config: List");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Inject
   private Configuration userConfig;
   
   @Inject
   private ProjectFactory projectFactory;
   
   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Shell shell = (Shell) context.getUIContext().getProvider();
      PrintStream out = shell.getOutput().out();

      Project project = Projects.getSelectedProject(projectFactory, context.getUIContext());
      Configuration projectConfig = null;
      
      if (project != null)
      {
         projectConfig = project.getFacet(ConfigurationFacet.class).getConfiguration();
      }
      
      Iterator<?> userConfigKeys = userConfig.getKeys();

      while (userConfigKeys.hasNext())
      {
         Object key = userConfigKeys.next();

         if (key != null)
         {
            out.print(key.toString());
            out.print("=");
            
            out.print("user: [" + userConfig.getProperty(key.toString()) + "]");
            if (projectConfig != null)
            {
               out.print(", project: ");
               Object value = projectConfig.getProperty(key.toString());
               if (value != null)
               {
                  out.print("[" + value.toString() + "] ");
               }
               else
                  out.print("[]");
            }
         }
         out.println();
      }
      
      if (projectConfig != null)
      {
         Iterator<?> projectConfigKeys = projectConfig.getKeys();
         
         while (projectConfigKeys.hasNext())
         {
            String key = projectConfigKeys.next().toString();
            if (!userConfig.containsKey(key))
            {
               out.print(key.toString());
               out.print("=project: [");
               out.print(projectConfig.getProperty(key.toString()).toString() + "]");
            }
         }
      }
      return Results.success();
   }
}

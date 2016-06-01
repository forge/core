/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration.ui;

import java.io.PrintStream;
import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.annotation.predicate.NonGUIEnabledPredicate;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * Configuration commands
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ConfigCommand
{
   @Inject
   private Configuration userConfig;

   @Inject
   private ProjectFactory projectFactory;

   @Command(value = "Config: Set", categories = { "Configuration" }, enabled = NonGUIEnabledPredicate.class)
   public Result setConfigurationProperty(UIContext context,
            @Option(value = "key", required = true) String key,
            @Option(value = "value", required = true) String value,
            @Option(value = "local") boolean local) throws Exception
   {
      if (local)
      {
         Project project = Projects.getSelectedProject(projectFactory, context);
         if (project != null)
         {
            Configuration projectConfig = project.getFacet(ConfigurationFacet.class).getConfiguration();
            projectConfig.setProperty(key, value);
         }
         else
         {
            return Results.fail("No project found in current context. Can't store in local configuration.");
         }
      }
      else
      {
         userConfig.setProperty(key, value);
      }
      return Results.success();
   }

   @Command(value = "Config: Clear", categories = { "Configuration" }, enabled = NonGUIEnabledPredicate.class)
   public Result clearProperty(UIContext context,
            @Option(value = "key", required = true) String key,
            @Option(value = "local") boolean local) throws Exception
   {
      if (local)
      {
         Project project = Projects.getSelectedProject(projectFactory, context);
         if (project != null)
         {
            Configuration projectConfig = project.getFacet(ConfigurationFacet.class).getConfiguration();
            projectConfig.clearProperty(key);
         }
         else
         {
            return Results.fail("No project found in current context. Can't clear from local configuration.");
         }
      }
      else
      {
         userConfig.clearProperty(key);
      }
      return Results.success();
   }

   @Command(value = "Config: List", categories = { "Configuration" }, enabled = NonGUIEnabledPredicate.class)
   public void listConfiguration(UIContext context, UIOutput output)
   {
      PrintStream out = output.out();

      Project project = Projects.getSelectedProject(projectFactory, context);
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
            out.println();
         }
      }
   }

}

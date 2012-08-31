/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.events.PickupResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.template.CompiledTemplateResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("plugins")
@Help("Set up necessary features for Forge plugin development")
@RequiresProject
@RequiresFacet(ForgeAPIFacet.class)
public class PluginsPlugin implements Plugin
{
   @Inject
   private Event<InstallFacets> event;

   @Inject
   private Event<PickupResource> pickup;

   @Inject
   private Project project;

   @Inject
   private ShellPrompt prompt;

   @Inject
   private TemplateCompiler compiler;

   @SetupCommand
   public void setup(final PipeOut out)
   {
      if (!project.hasFacet(ForgeAPIFacet.class))
      {
         event.fire(new InstallFacets(ForgeAPIFacet.class));
         if (!project.hasFacet(ForgeAPIFacet.class))
         {
            throw new RuntimeException("Could not install Forge API");
         }
      }
      else
         ShellMessages.success(out, "Forge API is installed.");
   }

   @Command("new-plugin")
   public void newPlugin(final PipeOut out,
            @Option(required = true, name = "named", description = "The plugin name") final String pluginName,
            @Option(name = "alias", description = "The plugin alias") final String alias)
            throws FileNotFoundException
   {
      String pluginAlias = Strings.isNullOrEmpty(alias) ? pluginName.replaceAll("([^A-Za-z0-9-])|[Plugin]", "").toLowerCase() : alias;
      if (Strings.isNullOrEmpty(pluginAlias)) {
         throw new RuntimeException("You should specify a valid alias for this plugin");
      }
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String className = canonicalize(pluginName);
      String packg = prompt.promptCommon(
               "In which package you'd like to create [" + className + "], or enter for default",
               PromptType.JAVA_PACKAGE, java.getBasePackage());

      Map<Object, Object> context = new HashMap<Object, Object>();
      context.put("name", className);
      context.put("alias", pluginAlias);

      CompiledTemplateResource pluginSource = compiler.compileResource(getClass().getResourceAsStream(
               "/org/jboss/forge/dev/PluginTemplate.jv"));
      CompiledTemplateResource testSource = compiler.compileResource(getClass().getResourceAsStream(
               "/org/jboss/forge/dev/PluginTemplateTest.jv"));

      JavaResource pluginResource = java.saveJavaSource(JavaParser.parse(JavaClass.class, pluginSource.render(context))
               .setPackage(packg));
      java.saveTestJavaSource(JavaParser.parse(JavaClass.class, testSource.render(context)).setPackage(packg));

      pickup.fire(new PickupResource(pluginResource));
   }

   public String canonicalize(final String name)
   {
      StringBuilder result = new StringBuilder();
      String[] split = name.split("[^A-Za-z0-9]");
      for (String string : split)
      {
         result.append(Strings.capitalize(string));
      }
      return result.toString();
   }
}

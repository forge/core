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
package org.jboss.forge.dev;

import java.io.FileNotFoundException;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
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
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("plugins")
@RequiresProject
@Help("Set up necessary features for Forge plugin development")
public class NewAddonPlugin implements Plugin
{
   @Inject
   private Event<InstallFacets> event;

   @Inject
   private Event<PickupResource> pickup;

   @Inject
   private Project project;

   @Inject
   private ShellPrompt prompt;

   @Command("setup")
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
   public void newPlugin(final PipeOut out, @Option(required = true,
            name = "named", description = "The plugin name") final String pluginName,
            @Option(name = "defaultCommand") final boolean defaultCommand) throws FileNotFoundException
   {

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String className = Strings.capitalize(pluginName);
      String packg = prompt.promptCommon(
               "In which package you'd like to create [" + className + "], or enter for default:",
               PromptType.JAVA_PACKAGE, java.getBasePackage());

      JavaClass plugin = JavaParser.create(JavaClass.class);
      plugin.setPackage(packg);
      plugin.addInterface(Plugin.class);
      plugin.setName(className);

      plugin.addAnnotation(Alias.class).setStringValue(pluginName.toLowerCase());

      plugin.addField().setPrivate().setName("prompt").setType(ShellPrompt.class).addAnnotation(Inject.class);
      plugin.addImport(PipeOut.class);
      plugin.addImport(Option.class);

      Method<JavaClass> command = plugin
               .addMethod("public void run(PipeOut out, @Option(name=\"value\") final String arg) { System.out.println(\"Executed default command with value: \" + arg); }");

      if (defaultCommand)
         command.addAnnotation(DefaultCommand.class);
      else
         command.addAnnotation(Command.class).setStringValue("run");

      JavaResource javaResource = java.saveJavaSource(plugin);

      pickup.fire(new PickupResource(javaResource));
   }
}

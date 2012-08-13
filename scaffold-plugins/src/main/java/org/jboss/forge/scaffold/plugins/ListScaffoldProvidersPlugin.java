/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.scaffold.plugins;

import java.io.FileNotFoundException;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.bus.util.Annotations;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * Lists all the scaffold providers registered on this forge installation
 * 
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 * 
 */
@Alias("list-scaffold-providers")
@Topic("UI Generation & Scaffolding")
@Help("Lists all the scaffold providers registered on this forge installation")
public class ListScaffoldProvidersPlugin implements Plugin
{

   @Inject
   @Any
   private Instance<ScaffoldProvider> providers;

   @DefaultCommand
   public void listProviders(PipeOut out) throws FileNotFoundException
   {
      for (ScaffoldProvider scaffoldProvider : providers)
      {
         Class<? extends ScaffoldProvider> scaffoldClass = scaffoldProvider.getClass();
         String alias = ConstraintInspector.getName(scaffoldClass);

         String description;
         if (Annotations.isAnnotationPresent(scaffoldClass, Help.class))
         {
            description = Annotations.getAnnotation(scaffoldClass, Help.class).value();
         }
         else
         {
            description = "<no description found>";
         }
         out.print(ShellColor.BOLD, "* " + alias + " : ");
         out.println(description);
      }
   }
}

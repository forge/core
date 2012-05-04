/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

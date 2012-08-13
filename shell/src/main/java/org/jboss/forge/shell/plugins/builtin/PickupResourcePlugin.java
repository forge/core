/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.events.PickupResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.PathspecParser;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
@Alias("pick-up")
@Topic("File & Resources")
@Help("Picks up a specific resource to work with.")
@Singleton
public class PickupResourcePlugin implements Plugin
{
   private final Shell shell;
   private final ResourceFactory resourceFactory;

   @Inject
   public PickupResourcePlugin(final Shell shell, final ResourceFactory factory)
   {
      this.shell = shell;
      this.resourceFactory = factory;
   }

   void pickup(@Observes final PickupResource event)
   {
      run(event.getResource(), null);
   }

   @DefaultCommand
   public void run(@Option(required = false) Resource<?> resource,
            @Option(required = false, name = "find", shortName = "f") final String path)
   {

      if (path != null)
      {
         PathspecParser pathspecParser = new PathspecParser(resourceFactory, shell.getCurrentResource(), path);
         List<Resource<?>> targets = pathspecParser.search();

         if (targets.isEmpty())
         {
            shell.println("No such resource");
         }
         else if (targets.size() > 1)
         {
            shell.println("Multiple targets");
            shell.println("----------------");

            int offset = shell.getCurrentResource().getFullyQualifiedName().length();

            for (Resource<?> r : targets)
            {
               shell.println(" --> ." + r.getFullyQualifiedName().substring(offset));
            }
            return;
         }
         else
         {
            resource = targets.get(0);

         }
      }

      shell.setCurrentResource(resource);

      if (shell.getCurrentResource() == null)
      {
         shell.println("No such resource: " + resource);
         return;
      }

      shell.println("Picked up type <" + shell.getCurrentResource().getClass().getSimpleName() + ">: " + resource);
   }
}

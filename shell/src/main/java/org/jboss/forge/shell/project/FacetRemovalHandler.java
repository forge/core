/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.FacetActionAborted;
import org.jboss.forge.project.facets.events.FacetRemoved;
import org.jboss.forge.project.facets.events.RemoveFacets;
import org.jboss.forge.project.services.FacetFactory;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacetRemovalHandler
{
   @Inject
   private FacetFactory factory;

   @Inject
   private Shell shell;

   @Inject
   private Project project;

   @Inject
   private BeanManager manager;

   public void removeRequest(@Observes final RemoveFacets request)
   {
      List<Facet> removed = new ArrayList<Facet>();
      shell.printlnVerbose("Received Facet removal request " + request.getFacetTypes());
      if (!request.promptRequested()
               || shell.promptBoolean("An action has requested to remove the following facets from your project "
                        + request.getFacetTypes() + " continue?", true))
      {
         for (Class<? extends Facet> type : request.getFacetTypes())
         {
            Facet facet = factory.getFacet(type);
            if (project.hasFacet(type))
            {
               removed.addAll(remove(facet, false));
            }
            else
            {
               shell.printlnVerbose("Facet not installed" + type);
            }
         }
      }
      else if (request.promptRequested())
      {
         throw new FacetActionAborted("Facet installation aborted.");
      }

      for (Facet facet : removed)
      {
         manager.fireEvent(new FacetRemoved(facet));
      }
   }

   private Collection<Facet> remove(final Facet facet, final boolean prompt)
   {
      Collection<Facet> removed = performRemoval(facet, prompt);
      if (!removed.contains(facet))
      {
         ShellMessages.error(shell, "Failed to remove [" + ConstraintInspector.getName(facet.getClass())
                  + "]; there may be a mess!");
      }
      return removed;
   }

   private Collection<Facet> performRemoval(final Facet facet, final boolean prompt)
   {
      Collection<Facet> result = new ArrayList<Facet>();
      if (!project.hasFacet(facet.getClass()))
      {
         result.add(facet);
      }
      else
      {
         facet.setProject(project);

         if (facet.isInstalled() || project.hasFacet(facet.getClass()))
         {
            project.removeFacet(facet);
         }

         if (!facet.isInstalled())
         {
            ShellMessages.success(shell, "Removed [" + ConstraintInspector.getName(facet.getClass())
                     + "] successfully.");
            result.add(facet);
         }
      }
      return result;
   }
}

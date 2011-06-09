/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

package org.jboss.forge.shell.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.bus.EventBus;
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
   private EventBus bus;

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
         bus.enqueue(new FacetRemoved(facet));
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

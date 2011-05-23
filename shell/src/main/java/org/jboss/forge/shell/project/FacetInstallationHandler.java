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
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.bus.EventBus;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.FacetActionAborted;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.facets.events.FacetInstalled;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.services.FacetFactory;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.exceptions.Abort;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class FacetInstallationHandler
{
   @Inject
   private FacetFactory factory;

   @Inject
   private Shell shell;

   @Inject
   private Project project;

   @Inject
   private EventBus bus;

   public void installRequest(@Observes final InstallFacets request)
   {
      List<Facet> installed = new ArrayList<Facet>();
      shell.printlnVerbose("Received Facet installation request " + request.getFacetTypes());
      if (!request.promptRequested()
               || shell.promptBoolean("An action has requested to install the following facets into your project "
                        + request.getFacetTypes() + " continue?", true))
      {
         for (Class<? extends Facet> type : request.getFacetTypes())
         {
            Facet facet = factory.getFacet(type);
            if (!project.hasFacet(type))
            {
               install(facet, false);
               installed.add(facet);
            }
            else
            {
               shell.printlnVerbose("Facet type already installed" + type);
            }
         }
      }
      else if (request.promptRequested())
      {
         throw new FacetActionAborted("Facet installation aborted.");
      }

      for (Facet facet : installed)
      {
         bus.enqueue(new FacetInstalled(facet));
      }
   }

   private void install(final Facet facet, final boolean prompt)
   {
      if (!performInstallation(facet, prompt))
      {
         ShellMessages.error(shell, "Failed to install [" + ConstraintInspector.getName(facet.getClass())
                  + "]; there may be a mess!");
      }
   }

   private boolean performInstallation(final Facet facet, final boolean prompt)
   {
      if (project.hasFacet(facet.getClass()))
      {
         return true;
      }
      else
      {
         facet.setProject(project);

         try
         {
            installDependencies(facet, prompt);
            PackagingType type = updatePackaging(facet);

            if (!facet.isInstalled() || !project.hasFacet(facet.getClass()))
            {
               project.installFacet(facet);
            }

            if (type != null)
            {
               project.getFacet(PackagingFacet.class).setPackagingType(type);
            }

            if (facet.isInstalled())
            {
               ShellMessages.success(shell, "Installed [" + ConstraintInspector.getName(facet.getClass())
                           + "] successfully.");
               return true;
            }
         }
         catch (Abort e)
         {
            abort();
         }
      }
      return false;
   }

   private void installDependencies(final Facet facet, final boolean prompt) throws Abort
   {
      List<Class<? extends Facet>> deps = ConstraintInspector.getFacetDependencies(facet.getClass());

      if (!project.hasAllFacets(deps))
      {
         List<Class<? extends Facet>> missingDeps = new ArrayList<Class<? extends Facet>>();
         List<String> facetNames = new ArrayList<String>();
         for (Class<? extends Facet> f : deps)
         {
            if (!project.hasFacet(f))
            {
               facetNames.add(ConstraintInspector.getName(f));
               missingDeps.add(f);
            }
         }

         if (prompt && !shell.promptBoolean("The ["
                  + ConstraintInspector.getName(facet.getClass())
                  + "] facet depends on the following missing facet(s): "
                  + facetNames
                  + ". Install as well?"))
         {
            throw new Abort();
         }
         else
         {
            List<Facet> installed = new ArrayList<Facet>();
            for (Class<? extends Facet> d : missingDeps)
            {
               Facet instance = factory.getFacet(d);
               if (performInstallation(instance, prompt))
               {
                  installed.add(instance);
               }
               else
               {
                  // attempt to undo everything we've done so far
                  for (Facet f : installed)
                  {
                     if (!f.uninstall())
                     {
                        ShellMessages.info(shell,
                                    "Could not uninstall [" + ConstraintInspector.getName(f.getClass())
                                             + "]. Must be cleaned up manually.");
                     }
                     else
                     {
                        ShellMessages.info(shell,
                                    "Uninstalled facet [" + ConstraintInspector.getName(f.getClass())
                                             + "].");
                     }
                  }
                  throw new Abort();
               }
            }
         }
      }
   }

   private PackagingType updatePackaging(final Facet facet) throws Abort
   {
      List<PackagingType> types = ConstraintInspector.getCompatiblePackagingTypes(facet.getClass());
      String facetName = ConstraintInspector.getName(facet.getClass());

      PackagingType packaging = project.getFacet(PackagingFacet.class).getPackagingType();
      if (types.isEmpty() || types.contains(packaging))
      {
         return null;
      }
      else if (shell.promptBoolean("Facet ["
                     + facetName + "] requires packaging type(s) " + types
                     + ", but is currently [" + packaging
                     + "]. Update packaging? (Note: this could deactivate other plugins in your project.)"))
      {
         if (types.size() == 1)
         {
            return types.get(0);
         }
         else
         {
            return shell.promptChoiceTyped("Select a new packaging type:", types);
         }
      }
      else
      {
         throw new Abort();
      }
   }

   private void abort()
   {
      ShellMessages.info(shell, "Installation cancelled!");
   }
}

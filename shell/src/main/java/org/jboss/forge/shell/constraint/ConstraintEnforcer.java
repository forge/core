/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.constraint;

import java.util.Collection;
import java.util.List;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.shell.command.CommandMetadata;
import org.jboss.forge.shell.command.PluginMetadata;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ConstraintEnforcer
{
   public void verifyAvailable(final Project currentProject, final CommandMetadata command) throws NoProjectException,
            UnsatisfiedPackagingTypeException, UnsatisfiedFacetDependencyException
   {

      Class<? extends Plugin> type = command.getParent().getType();
      if (ConstraintInspector.requiresProject(type) && (currentProject == null))
      {
         throw new NoProjectException(
                  "Oops! That command needs an active project, but you don't seem to be working on one. " +
                           "Perhaps you should open a project or create a new one?");
      }
      else if (currentProject != null)
      {
         List<PackagingType> compatiblePackagingTypes = ConstraintInspector.getCompatiblePackagingTypes(type);
         PackagingType currentPackagingType = currentProject.getFacet(PackagingFacet.class).getPackagingType();
         if (!compatiblePackagingTypes.isEmpty()
                  && !compatiblePackagingTypes.contains(currentPackagingType))
         {
            throw new UnsatisfiedPackagingTypeException("Oops! The command [" + command.getName()
                     + "] requires one of the following packaging types " + compatiblePackagingTypes
                     + ", but the current packaging type is [" + currentPackagingType + "]");
         }

         Collection<Facet> currentFacets = currentProject.getFacets();
         List<Class<? extends Facet>> facetDependencies = ConstraintInspector.getFacetDependencies(type);
         if (!currentProject.hasAllFacets(facetDependencies))
         {
            facetDependencies.removeAll(currentFacets);
            throw new UnsatisfiedFacetDependencyException("Oops! The command [" + command.getName()
                     + "] depends on one or more Facet that is not installed " + facetDependencies + "");
         }
      }
   }

   public void verifyAvailable(final Project currentProject, final PluginMetadata plugin) throws NoProjectException,
            UnsatisfiedPackagingTypeException, UnsatisfiedFacetDependencyException
   {
      Class<? extends Plugin> type = plugin.getType();

      // check to make sure that if the plugin requires a project, we have one
      if (ConstraintInspector.requiresProject(type) && (currentProject == null))
      {
         throw new NoProjectException(
                  "Oops! The [" + plugin.getName()
                           + "] plugin needs an active project, but you don't seem to be working on one. " +
                           "Perhaps you should open a project or create a new one?");
      }
      else if (currentProject != null)
      {
         // check to make sure that the project packaging type is appropriate
         List<PackagingType> compatiblePackagingTypes = ConstraintInspector.getCompatiblePackagingTypes(type);
         if (!compatiblePackagingTypes.isEmpty())
         {
            PackagingType currentPackagingType = currentProject.getFacet(PackagingFacet.class).getPackagingType();
            if (!compatiblePackagingTypes.contains(currentPackagingType))
            {
               throw new UnsatisfiedPackagingTypeException("Oops! The [" + plugin.getName()
                        + "] plugin requires one of the following packaging types " + compatiblePackagingTypes
                        + ", but the current packaging type is [" + currentPackagingType + "]");
            }
         }

         // check to make sure all required facets are registered in the Project
         Collection<Facet> currentFacets = currentProject.getFacets();
         List<Class<? extends Facet>> facetDependencies = ConstraintInspector.getFacetDependencies(type);
         if (!currentProject.hasAllFacets(facetDependencies))
         {
            facetDependencies.removeAll(currentFacets);
            throw new UnsatisfiedFacetDependencyException("Oops! The [" + plugin.getName()
                     + "] plugin depends on one or more Facet that is not installed " + facetDependencies + "");
         }

         // check to make sure all required Facets are in an installed state
         for (Class<? extends Facet> facet : facetDependencies)
         {
            if (!currentProject.getFacet(facet).isInstalled())
            {
               throw new UnsatisfiedFacetDependencyException("Oops! The [" + plugin.getName()
                        + "] plugin depends on one or more Facet that is not registered but not correctly installed ["
                        + ConstraintInspector.getName(facet) + "]");
            }
         }
      }
   }

   public boolean isAvailable(final Project currentProject, final PluginMetadata plugin)
   {
      try
      {
         verifyAvailable(currentProject, plugin);
      }
      catch (ConstraintException e)
      {
         return false;
      }
      return true;
   }

   public boolean isAvailable(Project currentProject, CommandMetadata commandMetadata)
   {
      try
      {
         verifyAvailable(currentProject, commandMetadata);
      }
      catch (ConstraintException e)
      {
         return false;
      }
      return true;
   }
}

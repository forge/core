/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.impl;

import java.util.List;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependencies.util.NonSnapshotDependencyFilter;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.furnace.util.Strings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyInstallerImpl implements DependencyInstaller
{
   @Override
   public Dependency install(final Project project, final Dependency request)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      final Dependency dependency = deps.resolveProperties(request);

      // Exists in deps, no version change requested
      Dependency unversioned = getUnversioned(dependency);
      Dependency existing = deps.getEffectiveDependency(unversioned);
      Dependency existingManaged = deps.getEffectiveManagedDependency(unversioned);

      if (existing != null) // we already have the dep
      {
         if (!Strings.isNullOrEmpty(existing.getCoordinate().getVersion())
                  && (existing.getCoordinate().getVersion().equals(dependency.getCoordinate().getVersion())
                  || Strings.isNullOrEmpty(dependency.getCoordinate().getVersion())))
         {
            /*
             * The version is the same as requested, or no specific version was requested. No action required.
             */
            return existing;
         }
         else
         {
            /*
             * Version is different. Update it.
             */
            return updateAll(deps, dependency, unversioned);
         }
      }
      else if (existingManaged != null)
      {
         /*
          * we don't have a dependency, or the existing dependency did not have a version, but we do have a managed
          * dependency
          */
         if (!Strings.isNullOrEmpty(existingManaged.getCoordinate().getVersion())
                  && (existingManaged.getCoordinate().getVersion().equals(dependency.getCoordinate().getVersion())
                  || Strings.isNullOrEmpty(dependency.getCoordinate().getVersion())))
         {
            /*
             * We have a version already, or the version is the same as requested, or no specific version was requested.
             * No need to touch dep management because we already have the right version.
             */
            deps.removeDependency(dependency);
            updateDependency(deps, unversioned);
            return existingManaged;
         }
         else
         {
            /*
             * Version is different or unspecified, and we had no existing managed dependency.
             */
            return updateAll(deps, dependency, unversioned);
         }
      }
      else
      {
         Dependency toInstall = dependency;
         if (Strings.isNullOrEmpty(dependency.getCoordinate().getVersion()))
         {
            List<Coordinate> versions = deps.resolveAvailableVersions(DependencyQueryBuilder.create(
                     dependency.getCoordinate()).setFilter(new NonSnapshotDependencyFilter()));

            if (versions.isEmpty())
               versions = deps.resolveAvailableVersions(DependencyQueryBuilder.create(
                        dependency.getCoordinate()));

            if (!versions.isEmpty())
               toInstall = DependencyBuilder.create(dependency).setVersion(
                        versions.get(versions.size() - 1).getVersion());
            else
            {
               throw new IllegalStateException("Could not resolve version for dependency ["
                        + dependency.getCoordinate() + "].");
            }
         }
         return updateAll(deps, toInstall, unversioned);
      }
   }

   private Dependency getUnversioned(Dependency dependency)
   {
      return DependencyBuilder.create(dependency).setVersion(null).setOptional(dependency.isOptional());
   }

   @Override
   public Dependency installManaged(Project project, Dependency dependency)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      updateManagedDependency(deps, dependency);
      return dependency;
   }

   @Override
   public boolean isInstalled(final Project project, final Dependency dependency)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      return deps.hasEffectiveDependency(dependency);
   }

   @Override
   public boolean isManaged(final Project project, final Dependency dependency)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      return deps.hasEffectiveManagedDependency(dependency);
   }

   private Dependency updateAll(final DependencyFacet deps, final Dependency requested,
            final Dependency unversioned)
   {
      updateDependency(deps, unversioned);
      updateManagedDependency(deps, requested);
      return requested;
   }

   private void updateManagedDependency(final DependencyFacet deps, final Dependency dependency)
   {
      deps.addDirectManagedDependency(dependency);
   }

   private void updateDependency(final DependencyFacet deps, final Dependency dependency)
   {
      deps.addDirectDependency(dependency);
   }

}
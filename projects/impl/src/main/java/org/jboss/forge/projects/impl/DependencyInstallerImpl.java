package org.jboss.forge.projects.impl;

import java.util.List;

import org.jboss.forge.container.util.Strings;
import org.jboss.forge.dependencies.Coordinate;
import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.dependencies.util.NonSnapshotDependencyFilter;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.dependencies.DependencyInstaller;
import org.jboss.forge.projects.facets.DependencyFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyInstallerImpl implements DependencyInstaller
{
   @Override
   public Dependency install(final Project project, final Dependency dependency)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      // Exists in deps, no version change requested
      Dependency unversioned = getUnversioned(dependency);
      Dependency existing = deps.getEffectiveDependency(unversioned);
      Dependency existingManaged = deps.getManagedDependency(unversioned);

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
             * Version is different or unspecified, and we had no existing version.
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

            if (!versions.isEmpty())
               toInstall = DependencyBuilder.create(dependency).setVersion(
                        versions.get(versions.size() - 1).getVersion());
            else
            {
               throw new IllegalStateException("Could not resolve version for dependency ["
                        + dependency.getCoordinate() + "].");
            }
         }
         return updateAll(deps, toInstall, toInstall);
      }
   }

   private Dependency getUnversioned(Dependency dependency)
   {
      return DependencyBuilder.create(dependency).setVersion(null);
   }

   @Override
   public Dependency installManaged(Project project, Dependency dependency)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      if (Strings.isNullOrEmpty(dependency.getCoordinate().getVersion()))
      {
         // we didn't request a specific version
         updateManagedDependency(deps, dependency);
      }
      else
      {
         // we requested a specific version
         updateManagedDependency(deps, dependency);
      }

      return dependency;
   }

   @Override
   public boolean isInstalled(final Project project, final Dependency dependency)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      return deps.hasEffectiveDependency(dependency);
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
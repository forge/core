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
package org.jboss.forge.shell.project;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyInstallerImpl implements DependencyInstaller
{
   private final ShellPrompt prompt;

   @Inject
   public DependencyInstallerImpl(final ShellPrompt prompt)
   {
      this.prompt = prompt;
   }

   @Override
   public Dependency install(final Project project, final Dependency dependency)
   {
      return install(project, dependency, null);
   }

   @Override
   public Dependency install(final Project project, final Dependency dependency, final ScopeType type)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      // Exists in deps, no version change requested
      Dependency existing = deps.getEffectiveDependency(dependency);
      Dependency existingManaged = deps.getEffectiveManagedDependency(dependency);
      DependencyBuilder unversioned = getUnversioned(dependency, type);

      if (existing != null) // we already have the dep
      {
         if (!Strings.isNullOrEmpty(existing.getVersion())
                  && (existing.getVersion().equals(dependency.getVersion()) // the version is the same as requested
                  || Strings.isNullOrEmpty(dependency.getVersion()))) // or no specific version was requested
         {
            // take no action
            return existing;
         }
         else if (Strings.isNullOrEmpty(existing.getVersion()) // we have no existing version
                  && !Strings.isNullOrEmpty(dependency.getVersion())) // but we did request one
         {
            return updateAll(deps, dependency, unversioned);
         }
         else
         // version is different
         {
            return promptAndUpdateAll(deps, dependency, unversioned);
         }
      }
      else if (existingManaged != null) // we don't have a dependency, or the existing dependency did not have a
                                        // version, but we do have a managed dependency
      {
         if (!Strings.isNullOrEmpty(existingManaged.getVersion()) // we have a version already
                  && (existingManaged.getVersion().equals(dependency.getVersion()) // the version is the same as
                                                                                   // requested
                  || Strings.isNullOrEmpty(dependency.getVersion()))) // or no specific version was requested
         {
            // don't need to touch dep management because we already have the right version
            deps.removeDependency(dependency);
            updateDependency(deps, unversioned);
            return existingManaged;
         }
         else if (Strings.isNullOrEmpty(existingManaged.getVersion()) // we have no existing version
                  && !Strings.isNullOrEmpty(dependency.getVersion())) // but we did request one
         {
            return updateAll(deps, dependency, unversioned);
         }
         else
         // version is different or unspecified, and we had no existing version.
         {
            return promptAndUpdateAll(deps, dependency, unversioned);
         }
      }
      else
      // we have neither dep or managed dep
      {
         if (Strings.isNullOrEmpty(dependency.getVersion()))
            // we didn't request a specific version
            return promptAndUpdateAll(deps, dependency, unversioned);
         else
            // we requested a specific version
            return updateAll(deps, dependency, unversioned);
      }
   }

   @Override
   public Dependency installManaged(Project project, Dependency dependency)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);

      if (Strings.isNullOrEmpty(dependency.getVersion()))
         // we didn't request a specific version
         return promptAndUpdateManaged(deps, dependency);
      else
         // we requested a specific version
         updateManagedDependency(deps, dependency);
      return dependency;
   }

   @Override
   public Dependency installManaged(Project project, Dependency dependency, ScopeType type)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      DependencyBuilder withScopeType = getWithScopeType(dependency, type);

      if (Strings.isNullOrEmpty(dependency.getVersion()))
         // we didn't request a specific version
         return promptAndUpdateManaged(deps, withScopeType);
      else
         // we requested a specific version
         updateManagedDependency(deps, withScopeType);
      return withScopeType;
   }

   @Override
   public boolean isInstalled(final Project project, final Dependency dependency)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      return deps.hasEffectiveDependency(dependency);
   }

   /*
    * Helpers
    */
   private DependencyBuilder getUnversioned(final Dependency dependency, final ScopeType type)
   {
      return getWithScopeType(dependency, type).setVersion(null);
   }

   private DependencyBuilder getWithScopeType(final Dependency dependency, final ScopeType type)
   {
      return DependencyBuilder.create(dependency).setVersion(dependency.getVersion())
               .setScopeType(type == null ? dependency.getScopeType() : type.toString())
               .setPackagingType(dependency.getPackagingType());
   }

   private Dependency promptAndUpdateAll(final DependencyFacet deps, final Dependency dependency,
            final DependencyBuilder unversioned)
   {
      DependencyBuilder toAdd = DependencyBuilder.create(promptVersion(deps, dependency));

      // ensure that the added managed dependency has the same traits as the dependency provided
      toAdd.setScopeType(dependency.getScopeType())
               .setClassifier(dependency.getClassifier())
               .setPackagingType(dependency.getPackagingType());

      updateAll(deps, toAdd, unversioned);
      return toAdd;
   }

   private Dependency promptAndUpdateManaged(final DependencyFacet deps, final Dependency dependency)
   {
      DependencyBuilder toAdd = DependencyBuilder.create(promptVersion(deps, dependency));

      // ensure that the added managed dependency has the same traits as the dependency provided
      toAdd.setScopeType(dependency.getScopeType())
               .setClassifier(dependency.getClassifier())
               .setPackagingType(dependency.getPackagingType());

      updateManagedDependency(deps, toAdd);
      return toAdd;
   }

   private Dependency updateAll(final DependencyFacet deps, final Dependency dependency,
            final DependencyBuilder unversioned)
   {
      updateDependency(deps, unversioned);
      updateManagedDependency(deps, dependency);
      return dependency;
   }

   private void updateManagedDependency(final DependencyFacet deps, final Dependency dependency)
   {
      deps.addDirectManagedDependency(dependency);
   }

   private void updateDependency(final DependencyFacet deps, final DependencyBuilder dependency)
   {
      deps.addDirectDependency(dependency);
   }

   private Dependency promptVersion(final DependencyFacet deps, final Dependency dependency)
   {
      Dependency result = dependency;
      final List<Dependency> versions = deps.resolveAvailableVersions(dependency);
      if (versions.size() > 0)
      {
         Dependency deflt = versions.get(versions.size() - 1);
         result = prompt.promptChoiceTyped("Use which version of '" + dependency.getArtifactId()
                  + "' ?", versions, deflt);
      }
      return result;
   }
}

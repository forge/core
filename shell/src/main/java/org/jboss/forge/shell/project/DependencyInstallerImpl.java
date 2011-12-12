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
      Dependency result = dependency;

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      if (deps.hasDirectDependency(dependency))
      {
         deps.removeDependency(deps.getDependency(dependency));
      }

      Dependency managedDependency = deps.getEffectiveManagedDependency(dependency);

      if (Strings.isNullOrEmpty(dependency.getVersion())
               && deps.hasEffectiveManagedDependency(dependency))
      {

         if (!Strings.isNullOrEmpty(managedDependency.getVersion()))
         {
            result = DependencyBuilder.create(managedDependency).setVersion(null)
                     .setScopeType(type);
            deps.addDependency(result);
         }
      }
      else
      {
         final List<Dependency> versions = deps.resolveAvailableVersions(dependency);
         Dependency deflt = versions.get(versions.size() - 1);

         if (managedDependency != null)
         {
            for (Dependency d : versions) {
               if (!Strings.isNullOrEmpty(managedDependency.getVersion())
                        && managedDependency.getVersion().equals(d.getVersion()))
               {
                  deflt = d;
                  break;
               }
            }
         }

         result = prompt.promptChoiceTyped("Which version of '" + dependency.getArtifactId()
                  + "' would you like to use?", versions, deflt);
         result = DependencyBuilder.create(result).setScopeType(type);
         deps.addDependency(result);
      }

      return result;
   }

   @Override
   public boolean isInstalled(final Project project, final Dependency dependency)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      return deps.hasDependency(dependency);
   }
}

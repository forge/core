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
package org.jboss.forge.spec.javaee;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * A base facet implementation for Facets which require Java EE library APIs to be installed.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RequiresFacet({ DependencyFacet.class })
public abstract class BaseJavaEEFacet extends BaseFacet
{
   public static final Dependency JAVAEE6 =
            DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0:2.0.0.Final:import:basic");

   private final DependencyInstaller installer;

   @Inject
   public BaseJavaEEFacet(final DependencyInstaller installer)
   {
      this.installer = installer;
   }

   @Override
   public boolean install()
   {
      for (Dependency requirement : getRequiredDependencies()) {
         if (!installer.isInstalled(project, requirement))
         {
            DependencyFacet deps = project.getFacet(DependencyFacet.class);
            if (!deps.hasEffectiveManagedDependency(requirement) && !deps.hasDirectManagedDependency(JAVAEE6))
            {
               deps.addDirectManagedDependency(JAVAEE6);
            }
            installer.install(project, requirement, ScopeType.PROVIDED);
         }
      }
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      for (Dependency requirement : getRequiredDependencies()) {
         if (!deps.hasEffectiveDependency(requirement))
         {
            return false;
         }
      }
      return true;
   }

   abstract protected List<Dependency> getRequiredDependencies();
}

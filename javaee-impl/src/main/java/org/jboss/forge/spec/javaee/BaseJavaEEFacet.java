/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.jboss.forge.project.packaging.PackagingType;
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
            DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0").setScopeType(ScopeType.IMPORT).setPackagingType(PackagingType.BASIC);

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
         if (!getInstaller().isInstalled(project, requirement))
         {
            DependencyFacet deps = project.getFacet(DependencyFacet.class);
            if (!deps.hasEffectiveManagedDependency(requirement) && !deps.hasDirectManagedDependency(JAVAEE6))
            {
               getInstaller().installManaged(project, JAVAEE6);
            }
            getInstaller().install(project, requirement, ScopeType.PROVIDED);
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

   public DependencyInstaller getInstaller()
   {
      return installer;
   }
}

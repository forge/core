/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.javaee;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.dependencies.Dependency;
import org.jboss.forge.dependencies.builder.DependencyBuilder;
import org.jboss.forge.facets.AbstractFacet;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFacet;
import org.jboss.forge.projects.dependencies.DependencyInstaller;
import org.jboss.forge.projects.facets.DependencyFacet;

/**
 * A base facet implementation for Facets which require Java EE library APIs to be installed.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public abstract class BaseJavaEEFacet extends AbstractFacet<Project> implements ProjectFacet
{
   // Version is statically set
   protected static final Dependency JAVAEE6 =
            DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0").setScopeType("import")
                     .setPackaging("pom").setVersion("3.0.2.Final");

   private final DependencyInstaller installer;

   @Inject
   public BaseJavaEEFacet(final DependencyInstaller installer)
   {
      this.installer = installer;
   }

   @Override
   public boolean install()
   {
      Project project = getOrigin();
      for (Dependency requirement : getRequiredDependencies())
      {
         if (!getInstaller().isInstalled(project, requirement))
         {
            DependencyFacet deps = project.getFacet(DependencyFacet.class);
            if (!deps.hasEffectiveManagedDependency(requirement) && !deps.hasDirectManagedDependency(JAVAEE6))
            {
               getInstaller().installManaged(project, JAVAEE6);
            }
            Dependency providedDep = DependencyBuilder.create(requirement).setScopeType("provided");
            getInstaller().install(project, providedDep);
         }
      }
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      Project project = getOrigin();
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      for (Dependency requirement : getRequiredDependencies())
      {
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

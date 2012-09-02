/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.parser.java.util.Assert;

/**
 * Builds a {@link DependencyQuery} object
 * 
 * @author George Gastaldi <ggastald@redhat.com>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyQueryBuilder implements DependencyQuery
{
   private Dependency dependency;
   private DependencyFilter dependencyFilter;
   private List<DependencyRepository> dependencyRepositories = new ArrayList<DependencyRepository>();

   protected DependencyQueryBuilder()
   {
   }

   /**
    * @deprecated Use {@link DependencyQueryBuilder#create()} instead.
    */
   @Deprecated
   public DependencyQueryBuilder(Dependency dependency)
   {
      setDependency(dependency);
   }

   public static DependencyQueryBuilder create(Dependency dependency)
   {
      return new DependencyQueryBuilder(dependency);
   }

   private void setDependency(Dependency dependency)
   {
      Assert.notNull(dependency, "Dependency must be set");
      this.dependency = dependency;
   }

   public DependencyQueryBuilder setFilter(DependencyFilter dependencyFilter)
   {
      this.dependencyFilter = dependencyFilter;
      return this;
   }

   public DependencyQueryBuilder setRepositories(DependencyRepository... dependencyRepositories)
   {
      return setRepositories(Arrays.asList(dependencyRepositories));
   }

   public DependencyQueryBuilder setRepositories(Iterable<DependencyRepository> dependencyRepositories)
   {
      if (dependencyRepositories != null)
      {
         for (DependencyRepository dependencyRepository : dependencyRepositories)
         {
            this.dependencyRepositories.add(dependencyRepository);
         }
      }
      return this;
   }

   public Dependency getDependency()
   {
      return dependency;
   }

   public DependencyFilter getDependencyFilter()
   {
      return dependencyFilter;
   }

   public List<DependencyRepository> getDependencyRepositories()
   {
      return dependencyRepositories;
   }

}

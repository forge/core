package org.jboss.forge.project.dependencies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builds a {@link DependencyQuery} object
 *
 * @author George Gastaldi <ggastald@redhat.com>
 *
 */
public class DependencyQueryBuilder
{
   private Dependency dependency;
   private DependencyFilter dependencyFilter;
   private List<DependencyRepository> dependencyRepositories = new ArrayList<DependencyRepository>();

   public DependencyQueryBuilder(Dependency dependency)
   {
      assert dependency != null;
      if (dependency == null)
      {
         throw new IllegalStateException("Dependency must be set");
      }
      this.dependency = dependency;
   }

   public DependencyQueryBuilder withFilter(DependencyFilter dependencyFilter)
   {
      this.dependencyFilter = dependencyFilter;
      return this;
   }

   public DependencyQueryBuilder withRepositories(DependencyRepository... dependencyRepositories)
   {
      withRepositories(Arrays.asList(dependencyRepositories));
      return this;
   }

   public DependencyQueryBuilder withRepositories(Iterable<DependencyRepository> dependencyRepositories)
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

   public DependencyQueryBuilder withoutSnapshots()
   {
      return withFilter(new NonSnapshotDependencyFilter());
   }

   public DependencyQuery build()
   {
      return new DependencyQuery(dependency, dependencyRepositories, dependencyFilter);
   }
}

package org.jboss.forge.project.dependencies;

import java.util.List;

/**
 * A parameter object
 *
 * @author George Gastaldi <ggastald@redhat.com>
 *
 */
public class DependencyQuery
{
   private Dependency dependency;
   private List<DependencyRepository> dependencyRepositories;
   private DependencyFilter dependencyFilter;

   /**
    * Constructor is package-protected, use {@link DependencyQueryBuilder} to create a {@link DependencyQuery} object
    *
    * @param dependency
    * @param dependencyRepositories
    * @param dependencyFilter
    */
   DependencyQuery(Dependency dependency, List<DependencyRepository> dependencyRepositories,
            DependencyFilter dependencyFilter)
   {
      super();
      this.dependency = dependency;
      this.dependencyRepositories = dependencyRepositories;
      this.dependencyFilter = dependencyFilter;
   }

   public Dependency getDependency()
   {
      return dependency;
   }

   public List<DependencyRepository> getDependencyRepositories()
   {
      return dependencyRepositories;
   }

   public DependencyFilter getDependencyFilter()
   {
      return dependencyFilter;
   }

}
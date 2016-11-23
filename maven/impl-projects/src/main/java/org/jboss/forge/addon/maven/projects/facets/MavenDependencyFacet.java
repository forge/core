/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.ProjectBuildingResult;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependencies.util.Dependencies;
import org.jboss.forge.addon.dependencies.util.NonSnapshotDependencyFilter;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.dependencies.MavenDependencyAdapter;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenFacetImpl;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Strings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(MavenFacet.class)
public class MavenDependencyFacet extends AbstractFacet<Project> implements DependencyFacet
{
   private static final Logger log = Logger.getLogger(MavenDependencyFacet.class.getName());

   private DependencyResolver resolver;

   @Override
   public boolean isInstalled()
   {
      return getFaceted().hasFacet(MavenFacet.class);
   }

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public void addDirectDependency(final Dependency dep)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      List<org.apache.maven.model.Dependency> dependencies = pom.getDependencies();
      Coordinate depCoordinate = dep.getCoordinate();
      boolean exists = false;
      for (org.apache.maven.model.Dependency dependency : dependencies)
      {
         Coordinate dependencyCoordinate = new MavenDependencyAdapter(dependency).getCoordinate();
         if (Dependencies.areEquivalent(depCoordinate, dependencyCoordinate))
         {
            dependency.setVersion(depCoordinate.getVersion());
            exists = true;
         }
      }
      if (!exists)
      {
         org.apache.maven.model.Dependency dependency = MavenDependencyAdapter.toMavenList(Arrays.asList(dep)).get(0);
         dependencies.add(dependency);
      }
      maven.setModel(pom);
   }

   @Override
   public boolean hasDirectDependency(final Dependency dependency)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      for (Dependency dep : dependencies)
      {
         if (Dependencies.areEquivalent(resolveProperties(dependency), dep))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public void removeDependency(final Dependency dep)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      List<Dependency> toBeRemoved = new ArrayList<>();
      for (Dependency dependency : dependencies)
      {
         if (Dependencies.areEquivalent(dependency, resolveProperties(dep)))
         {
            toBeRemoved.add(dependency);
         }
      }
      dependencies.removeAll(toBeRemoved);
      pom.setDependencies(MavenDependencyAdapter.toMavenList(dependencies));
      maven.setModel(pom);
   }

   @Override
   public List<Dependency> getDependencies()
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      List<Dependency> result = new ArrayList<>();
      for (Dependency dependency : dependencies)
      {
         result.add(resolveProperties(dependency));
      }

      return result;
   }

   @Override
   public Dependency getDirectDependency(final Dependency dependency)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      for (Dependency dep : dependencies)
      {
         if (Dependencies.areEquivalent(resolveProperties(dependency), dep))
         {
            return resolveProperties(dep);
         }
      }
      return null;
   }

   @Override
   public boolean hasEffectiveDependency(final Dependency dependency)
   {
      return getEffectiveDependency(resolveProperties(dependency)) != null;
   }

   @Override
   public Dependency getEffectiveDependency(final Dependency manDep)
   {
      for (Dependency dependency : getEffectiveDependencies())
      {
         if (Dependencies.areEquivalent(dependency, resolveProperties(manDep)))
         {
            return resolveProperties(dependency);
         }
      }
      return null;
   }

   @Override
   public List<Dependency> getEffectiveDependencies()
   {
      List<Dependency> result = new ArrayList<>();

      MavenFacetImpl maven = getFaceted().getFacet(MavenFacetImpl.class);
      try
      {
         ProjectBuildingResult projectBuildingResult = maven.getProjectBuildingResult();
         DependencyResolutionResult dependencyResolutionResult = projectBuildingResult.getDependencyResolutionResult();
         List<Dependency> deps = MavenDependencyAdapter.fromAetherList(dependencyResolutionResult.getDependencies());

         for (Dependency dependency : deps)
         {
            result.add(resolveProperties(dependency));
         }
      }
      catch (Exception e)
      {
         log.log(Level.SEVERE, "Could not resolve managed dependencies in project ["
                  + maven.getModelResource().getFullyQualifiedName() + "]. ", e);
      }

      return result;
   }

   @Override
   public void addManagedDependency(final Dependency manDep)
   {
      if (!hasEffectiveManagedDependency(resolveProperties(manDep)))
      {
         addDirectManagedDependency(manDep);
      }
   }

   @Override
   public void addDirectManagedDependency(final Dependency dep)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      DependencyManagement depMan = pom.getDependencyManagement();
      depMan = depMan != null ? depMan : new DependencyManagement();

      List<Dependency> managedDependencies = MavenDependencyAdapter.fromMavenList(depMan.getDependencies());
      ListIterator<Dependency> managedDepsIterator = managedDependencies.listIterator();
      boolean found = false;
      while (managedDepsIterator.hasNext())
      {
         Dependency managedDependency = managedDepsIterator.next();
         if (Dependencies.areEquivalent(managedDependency, dep))
         {
            managedDepsIterator.set(dep);
            found = true;
         }
      }
      if (!found)
      {
         managedDependencies.add(dep);
      }
      depMan.setDependencies(MavenDependencyAdapter.toMavenList(managedDependencies));
      pom.setDependencyManagement(depMan);
      maven.setModel(pom);
   }

   @Override
   public boolean hasEffectiveManagedDependency(final Dependency manDep)
   {
      return (getEffectiveManagedDependency(resolveProperties(manDep)) != null);
   }

   @Override
   public Dependency getEffectiveManagedDependency(final Dependency manDep)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      try
      {
         DependencyManagement depMan = maven.getEffectiveModel().getDependencyManagement();
         List<Dependency> managedDependencies = (depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
                  .getDependencies()) : new ArrayList<>());

         for (Dependency managedDependency : managedDependencies)
         {
            if (Dependencies.areEquivalent(managedDependency, resolveProperties(manDep)))
            {
               return resolveProperties(managedDependency);
            }
         }
      }
      catch (Exception e)
      {
         log.log(Level.SEVERE, "Could not resolve managed dependencies in project ["
                  + maven.getModelResource().getFullyQualifiedName() + "]. ", e);
      }
      return null;
   }

   @Override
   public boolean hasDirectManagedDependency(final Dependency managedDependency)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      DependencyManagement depMan = pom.getDependencyManagement();

      List<Dependency> managedDependencies = depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
               .getDependencies()) : new ArrayList<>();

      for (Dependency manDep : managedDependencies)
      {
         if (Dependencies.areEquivalent(resolveProperties(managedDependency), manDep))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public void removeManagedDependency(final Dependency manDep)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      DependencyManagement depMan = pom.getDependencyManagement();
      depMan = depMan != null ? depMan : new DependencyManagement();

      List<Dependency> managedDependencies = MavenDependencyAdapter.fromMavenList(depMan.getDependencies());

      List<Dependency> toBeRemoved = new ArrayList<>();
      for (Dependency managedDependency : managedDependencies)
      {
         if (Dependencies.areEquivalent(managedDependency, manDep))
         {
            toBeRemoved.add(managedDependency);
         }
      }
      managedDependencies.removeAll(toBeRemoved);
      depMan.setDependencies(MavenDependencyAdapter.toMavenList(managedDependencies));
      pom.setDependencyManagement(depMan);
      maven.setModel(pom);
   }

   @Override
   public List<Dependency> getManagedDependencies()
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      DependencyManagement depMan = pom.getDependencyManagement();

      List<Dependency> managedDependencies = null;
      if (depMan != null)
         managedDependencies = MavenDependencyAdapter.fromMavenList(depMan.getDependencies());
      else
         managedDependencies = new ArrayList<>();

      List<Dependency> result = new ArrayList<>();
      for (Dependency dependency : managedDependencies)
      {
         result.add(resolveProperties(dependency));
      }
      return result;
   }

   @Override
   public Dependency getDirectManagedDependency(final Dependency manDep)
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      DependencyManagement depMan = pom.getDependencyManagement();

      List<Dependency> managedDependencies = depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
               .getDependencies()) : new ArrayList<>();

      for (Dependency managedDependency : managedDependencies)
      {
         if (Dependencies.areEquivalent(managedDependency, resolveProperties(manDep)))
         {
            return resolveProperties(managedDependency);
         }
      }
      return null;
   }

   @Override
   public Dependency resolveProperties(final Dependency dependency)
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);
      DependencyBuilder builder = DependencyBuilder.create(dependency);

      builder.setGroupId(mvn.resolveProperties(dependency.getCoordinate().getGroupId()));
      builder.setArtifactId(mvn.resolveProperties(dependency.getCoordinate().getArtifactId()));
      builder.setVersion(mvn.resolveProperties(dependency.getCoordinate().getVersion()));
      builder.setClassifier(mvn.resolveProperties(dependency.getCoordinate().getClassifier()));
      builder.setPackaging(mvn.resolveProperties(dependency.getCoordinate().getPackaging()));
      builder.setScopeType(mvn.resolveProperties(dependency.getScopeType()));

      return builder;
   }

   @Override
   public List<Coordinate> resolveAvailableVersions(final String gavs)
   {
      return resolveAvailableVersions(DependencyBuilder.create(gavs));
   }

   @Override
   public List<Coordinate> resolveAvailableVersions(final Dependency dep)
   {
      DependencyQueryBuilder query = DependencyQueryBuilder.create(dep.getCoordinate()).setRepositories(
               getRepositories());
      if (!Strings.isNullOrEmpty(dep.getCoordinate().getVersion())
               && !dep.getCoordinate().getVersion().contains("SNAPSHOT"))
      {
         query.setFilter(new NonSnapshotDependencyFilter());
      }
      List<Coordinate> versions = getResolver().resolveVersions(query);
      return versions;
   }

   @Override
   public List<Coordinate> resolveAvailableVersions(final DependencyQuery query)
   {
      List<Coordinate> versions = getResolver().resolveVersions(query);
      return versions;
   }

   @Override
   public void addRepository(final String name, final String url)
   {
      if (!hasRepository(url))
      {
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
         Model pom = maven.getModel();
         Repository repo = new Repository();
         repo.setId(name);
         repo.setUrl(url);
         pom.getRepositories().add(repo);
         maven.setModel(pom);
      }
   }

   @Override
   public List<DependencyRepository> getRepositories()
   {
      List<DependencyRepository> results = new ArrayList<>();
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      List<Repository> repos = pom.getRepositories();
      for (Repository repo : repos)
      {
         results.add(new DependencyRepository(repo.getId(), repo.getUrl()));
      }
      return Collections.unmodifiableList(results);
   }

   @Override
   public boolean hasRepository(final String url)
   {
      if (url != null)
      {
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
         Model pom = maven.getModel();
         List<Repository> repositories = pom.getRepositories();
         for (Repository repo : repositories)
         {
            if (repo.getUrl().trim().equals(url.trim()))
            {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public DependencyRepository removeRepository(final String url)
   {
      if (url != null)
      {
         MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
         Model pom = maven.getModel();
         List<Repository> repos = pom.getRepositories();
         for (Repository repo : repos)
         {
            if (repo.getUrl().equals(url.trim()))
            {
               repos.remove(repo);
               maven.setModel(pom);
               return new DependencyRepository(repo.getId(), repo.getUrl());
            }
         }
      }
      return null;

   }

   @Override
   public List<Dependency> getDependenciesInScopes(final String... scopes)
   {
      List<Dependency> result = new ArrayList<>();
      List<Dependency> dependencies = getDependencies();
      for (Dependency dependency : dependencies)
      {
         for (String scope : scopes)
         {
            if ((dependency.getScopeType() == null) || dependency.getScopeType().equals(scope))
            {
               dependency = resolveProperties(dependency);
               result.add(dependency);
               break;
            }
         }
      }
      return result;
   }

   @Override
   public List<Dependency> getEffectiveDependenciesInScopes(final String... scopes)
   {
      List<Dependency> result = new ArrayList<>();
      List<Dependency> dependencies = getEffectiveDependencies();
      for (Dependency dependency : dependencies)
      {
         for (String scope : scopes)
         {
            if ((dependency.getScopeType() == null) || dependency.getScopeType().equals(scope))
            {
               dependency = resolveProperties(dependency);
               result.add(dependency);
               break;
            }
         }
      }
      return result;
   }

   /**
    * @return the resolver
    */
   private DependencyResolver getResolver()
   {
      if (resolver == null)
         resolver = SimpleContainer.getServices(getClass().getClassLoader(), DependencyResolver.class).get();
      return resolver;
   }
}

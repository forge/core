/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
import org.jboss.forge.addon.maven.dependencies.MavenDependencyAdapter;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenFacetImpl;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
public class MavenDependencyFacet extends AbstractFacet<Project> implements DependencyFacet
{
   private final DependencyResolver resolver;

   @Inject
   public MavenDependencyFacet(final DependencyResolver resolver)
   {
      this.resolver = resolver;
   }

   @Override
   public boolean isInstalled()
   {
      return getOrigin().hasFacet(MavenFacet.class);
   }

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public void addDirectDependency(final Dependency dep)
   {
      removeDependency(dep);

      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());
      dependencies.add(dep);
      pom.setDependencies(MavenDependencyAdapter.toMavenList(dependencies));
      maven.setPOM(pom);
   }

   @Override
   public boolean hasDirectDependency(final Dependency dependency)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
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
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      List<Dependency> toBeRemoved = new ArrayList<Dependency>();
      for (Dependency dependency : dependencies)
      {
         if (Dependencies.areEquivalent(dependency, resolveProperties(dep)))
         {
            toBeRemoved.add(dependency);
         }
      }
      dependencies.removeAll(toBeRemoved);
      pom.setDependencies(MavenDependencyAdapter.toMavenList(dependencies));
      maven.setPOM(pom);
   }

   @Override
   public List<Dependency> getDependencies()
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      List<Dependency> result = new ArrayList<Dependency>();
      for (Dependency dependency : dependencies)
      {
         result.add(resolveProperties(dependency));
      }

      return result;
   }

   @Override
   public Dependency getDirectDependency(final Dependency dependency)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
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
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      ProjectBuildingResult projectBuildingResult = ((MavenFacetImpl) maven).getProjectBuildingResult();
      DependencyResolutionResult dependencyResolutionResult = projectBuildingResult.getDependencyResolutionResult();
      List<Dependency> deps = MavenDependencyAdapter.fromAetherList(dependencyResolutionResult.getDependencies());

      List<Dependency> result = new ArrayList<Dependency>();
      for (Dependency dependency : deps)
      {
         result.add(resolveProperties(dependency));
      }

      return result;
   }

   @Override
   public void addManagedDependency(final Dependency manDep)
   {
      if (!hasEffectiveManagedDependency(resolveProperties(manDep)))
      {
         MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
         Model pom = maven.getPOM();
         DependencyManagement depMan = pom.getDependencyManagement();
         depMan = depMan != null ? depMan : new DependencyManagement();

         List<Dependency> managedDependencies = MavenDependencyAdapter.fromMavenList(depMan.getDependencies());
         managedDependencies.add(manDep);
         depMan.setDependencies(MavenDependencyAdapter.toMavenList(managedDependencies));
         pom.setDependencyManagement(depMan);
         maven.setPOM(pom);
      }
   }

   @Override
   public void addDirectManagedDependency(final Dependency dep)
   {
      removeManagedDependency(dep);

      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      DependencyManagement depMan = pom.getDependencyManagement();
      depMan = depMan != null ? depMan : new DependencyManagement();

      List<Dependency> managedDependencies = MavenDependencyAdapter.fromMavenList(depMan.getDependencies());
      managedDependencies.add(dep);
      depMan.setDependencies(MavenDependencyAdapter.toMavenList(managedDependencies));
      pom.setDependencyManagement(depMan);
      maven.setPOM(pom);
   }

   @Override
   public boolean hasEffectiveManagedDependency(final Dependency manDep)
   {
      return (getEffectiveManagedDependency(resolveProperties(manDep)) != null);
   }

   @Override
   public Dependency getEffectiveManagedDependency(final Dependency manDep)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      DependencyManagement depMan = ((MavenFacetImpl) maven).getProjectBuildingResult().getProject()
               .getDependencyManagement();
      List<Dependency> managedDependencies = (depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
               .getDependencies()) : new ArrayList<Dependency>());

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
   public boolean hasDirectManagedDependency(final Dependency managedDependency)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      DependencyManagement depMan = pom.getDependencyManagement();

      List<Dependency> managedDependencies = depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
               .getDependencies()) : new ArrayList<Dependency>();

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
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      DependencyManagement depMan = pom.getDependencyManagement();
      depMan = depMan != null ? depMan : new DependencyManagement();

      List<Dependency> managedDependencies = MavenDependencyAdapter.fromMavenList(depMan.getDependencies());

      List<Dependency> toBeRemoved = new ArrayList<Dependency>();
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
      maven.setPOM(pom);
   }

   @Override
   public List<Dependency> getManagedDependencies()
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      DependencyManagement depMan = pom.getDependencyManagement();

      List<Dependency> managedDependencies = null;
      if (depMan != null)
         managedDependencies = MavenDependencyAdapter.fromMavenList(depMan.getDependencies());
      else
         managedDependencies = new ArrayList<Dependency>();

      List<Dependency> result = new ArrayList<Dependency>();
      for (Dependency dependency : managedDependencies)
      {
         result.add(resolveProperties(dependency));
      }
      return result;
   }

   @Override
   public Dependency getManagedDependency(final Dependency manDep)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      DependencyManagement depMan = pom.getDependencyManagement();

      List<Dependency> managedDependencies = depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
               .getDependencies()) : new ArrayList<Dependency>();

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
   public Map<String, String> getProperties()
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      Map<String, String> result = new HashMap<String, String>();
      for (Entry<Object, Object> o : properties.entrySet())
      {
         result.put((String) o.getKey(), (String) o.getValue());
      }
      return result;
   }

   @Override
   public void setProperty(final String name, final String value)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      properties.put(name, value);
      maven.setPOM(pom);
   }

   @Override
   public String getProperty(final String name)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      maven.setPOM(pom);
      return (String) properties.get(name);
   }

   @Override
   public Dependency resolveProperties(final Dependency dependency)
   {
      MavenFacet mvn = getOrigin().getFacet(MavenFacet.class);
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
   public String removeProperty(final String name)
   {
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      String result = (String) properties.remove(name);
      maven.setPOM(pom);
      return result;
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
      if (dep.getCoordinate().getVersion() != null && !dep.getCoordinate().getVersion().contains("SNAPSHOT"))
      {
         query.setFilter(new NonSnapshotDependencyFilter());
      }
      List<Coordinate> versions = resolver.resolveVersions(query);
      return versions;
   }

   @Override
   public List<Coordinate> resolveAvailableVersions(final DependencyQuery query)
   {
      List<Coordinate> versions = resolver.resolveVersions(query);
      return versions;
   }

   @Override
   public void addRepository(final String name, final String url)
   {
      if (!hasRepository(url))
      {
         MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
         Model pom = maven.getPOM();
         Repository repo = new Repository();
         repo.setId(name);
         repo.setUrl(url);
         pom.getRepositories().add(repo);
         maven.setPOM(pom);
      }
   }

   @Override
   public List<DependencyRepository> getRepositories()
   {
      List<DependencyRepository> results = new ArrayList<DependencyRepository>();
      MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
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
         MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
         Model pom = maven.getPOM();
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
         MavenFacet maven = getOrigin().getFacet(MavenFacet.class);
         Model pom = maven.getPOM();
         List<Repository> repos = pom.getRepositories();
         for (Repository repo : repos)
         {
            if (repo.getUrl().equals(url.trim()))
            {
               repos.remove(repo);
               maven.setPOM(pom);
               return new DependencyRepository(repo.getId(), repo.getUrl());
            }
         }
      }
      return null;

   }

   @Override
   public List<Dependency> getDependenciesInScopes(final String... scopes)
   {
      List<Dependency> result = new ArrayList<Dependency>();
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
      List<Dependency> result = new ArrayList<Dependency>();
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

   @Override
   public void setOrigin(Project project)
   {
      super.setOrigin(project);
   }
}

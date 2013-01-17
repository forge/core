/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.facets;

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
import org.jboss.forge.bus.EventBus;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.dependencies.MavenDependencyAdapter;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyQuery;
import org.jboss.forge.project.dependencies.DependencyQueryBuilder;
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.jboss.forge.project.dependencies.DependencyResolver;
import org.jboss.forge.project.dependencies.NonSnapshotDependencyFilter;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.dependencies.events.AddedDependencies;
import org.jboss.forge.project.dependencies.events.RemovedDependencies;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
@Alias("forge.maven.MavenDependencyFacet")
@RequiresFacet(MavenCoreFacet.class)
public class MavenDependencyFacet extends BaseFacet implements DependencyFacet, Facet
{
   private final DependencyResolver resolver;

   private final EventBus bus;

   @Inject
   public MavenDependencyFacet(final DependencyResolver resolver, final EventBus bus)
   {
      this.resolver = resolver;
      this.bus = bus;
   }

   @Override
   public boolean isInstalled()
   {
      try
      {
         project.getFacet(MavenCoreFacet.class);
         return true;
      }
      catch (FacetNotFoundException e)
      {
         return false;
      }
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

      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());
      dependencies.add(dep);
      pom.setDependencies(MavenDependencyAdapter.toMavenList(dependencies));
      maven.setPOM(pom);
      bus.enqueue(new AddedDependencies(project, dep));
   }

   @Override
   public boolean hasDirectDependency(final Dependency dependency)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      for (Dependency dep : dependencies)
      {
         if (DependencyBuilder.areEquivalent(resolveProperties(dependency), dep))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public void removeDependency(final Dependency dep)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      List<Dependency> toBeRemoved = new ArrayList<Dependency>();
      for (Dependency dependency : dependencies)
      {
         if (DependencyBuilder.areEquivalent(dependency, resolveProperties(dep)))
         {
            toBeRemoved.add(dependency);
         }
      }
      dependencies.removeAll(toBeRemoved);
      pom.setDependencies(MavenDependencyAdapter.toMavenList(dependencies));
      maven.setPOM(pom);
      bus.enqueue(new RemovedDependencies(project, toBeRemoved));
   }

   @Override
   public List<Dependency> getDependencies()
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
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
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      for (Dependency dep : dependencies)
      {
         if (DependencyBuilder.areEquivalent(resolveProperties(dependency), dep))
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
         if (DependencyBuilder.areEquivalent(dependency, resolveProperties(manDep)))
         {
            return resolveProperties(dependency);
         }
      }
      return null;
   }

   @Override
   public List<Dependency> getEffectiveDependencies()
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      List<Dependency> deps = MavenDependencyAdapter.fromAetherList(
               maven.getFullProjectBuildingResult()
                        .getDependencyResolutionResult()
                        .getDependencies()
               );

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
         MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
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

      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
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
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      DependencyManagement depMan = maven.getFullProjectBuildingResult().getProject().getDependencyManagement();
      List<Dependency> managedDependencies = (depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
               .getDependencies()) : new ArrayList<Dependency>());

      for (Dependency managedDependency : managedDependencies)
      {
         if (DependencyBuilder.areEquivalent(managedDependency, resolveProperties(manDep)))
         {
            return resolveProperties(managedDependency);
         }
      }
      return null;
   }

   @Override
   public boolean hasDirectManagedDependency(final Dependency managedDependency)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      DependencyManagement depMan = pom.getDependencyManagement();

      List<Dependency> managedDependencies = depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
               .getDependencies()) : new ArrayList<Dependency>();

      for (Dependency manDep : managedDependencies)
      {
         if (DependencyBuilder.areEquivalent(resolveProperties(managedDependency), manDep))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public void removeManagedDependency(final Dependency manDep)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      DependencyManagement depMan = pom.getDependencyManagement();
      depMan = depMan != null ? depMan : new DependencyManagement();

      List<Dependency> managedDependencies = MavenDependencyAdapter.fromMavenList(depMan.getDependencies());

      List<Dependency> toBeRemoved = new ArrayList<Dependency>();
      for (Dependency managedDependency : managedDependencies)
      {
         if (DependencyBuilder.areEquivalent(managedDependency, manDep))
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
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
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
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      DependencyManagement depMan = pom.getDependencyManagement();

      List<Dependency> managedDependencies = depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
               .getDependencies()) : new ArrayList<Dependency>();

      for (Dependency managedDependency : managedDependencies)
      {
         if (DependencyBuilder.areEquivalent(managedDependency, resolveProperties(manDep)))
         {
            return resolveProperties(managedDependency);
         }
      }
      return null;
   }

   @Override
   public Map<String, String> getProperties()
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
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
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      properties.put(name, value);
      maven.setPOM(pom);
   }

   @Override
   public String getProperty(final String name)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      maven.setPOM(pom);
      return (String) properties.get(name);
   }

   @Override
   public Dependency resolveProperties(final Dependency dependency)
   {
      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);
      DependencyBuilder builder = DependencyBuilder.create(dependency);

      builder.setGroupId(mvn.resolveProperties(dependency.getGroupId()));
      builder.setArtifactId(mvn.resolveProperties(dependency.getArtifactId()));
      builder.setVersion(mvn.resolveProperties(dependency.getVersion()));
      builder.setClassifier(mvn.resolveProperties(dependency.getClassifier()));
      builder.setPackagingType(mvn.resolveProperties(dependency.getPackagingType()));
      builder.setScopeType(mvn.resolveProperties(dependency.getScopeType()));

      return builder;
   }

   @Override
   public String removeProperty(final String name)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();

      Properties properties = pom.getProperties();
      String result = (String) properties.remove(name);
      maven.setPOM(pom);
      return result;
   }

   @Override
   public List<Dependency> resolveAvailableVersions(final String gavs)
   {
      return resolveAvailableVersions(DependencyBuilder.create(gavs));
   }

   @Override
   public List<Dependency> resolveAvailableVersions(final Dependency dep)
   {
      DependencyQueryBuilder query = DependencyQueryBuilder.create(dep).setRepositories(getRepositories());
      if (dep.getVersion() != null && !dep.getVersion().contains("SNAPSHOT"))
      {
         query.setFilter(new NonSnapshotDependencyFilter());
      }
      List<Dependency> versions = resolver.resolveVersions(query);
      return versions;
   }

   @Override
   public List<Dependency> resolveAvailableVersions(final DependencyQuery query)
   {
      List<Dependency> versions = resolver.resolveVersions(query);
      return versions;
   }

   @Override
   public void addRepository(final String name, final String url)
   {
      if (!hasRepository(url))
      {
         MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
         Model pom = maven.getPOM();
         Repository repo = new Repository();
         repo.setId(name);
         repo.setUrl(url);
         pom.getRepositories().add(repo);
         maven.setPOM(pom);
      }
   }

   @Override
   public void addRepository(final KnownRepository repository)
   {
      addRepository(repository.name(), repository.getUrl());
   }

   @Override
   public List<DependencyRepository> getRepositories()
   {
      List<DependencyRepository> results = new ArrayList<DependencyRepository>();
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      List<Repository> repos = pom.getRepositories();
      for (Repository repo : repos)
      {
         results.add(new DependencyRepositoryImpl(repo.getId(), repo.getUrl()));
      }
      return Collections.unmodifiableList(results);
   }

   @Override
   public boolean hasRepository(final String url)
   {
      if (url != null)
      {
         MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
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
   public boolean hasRepository(final KnownRepository repository)
   {
      return hasRepository(repository.getUrl());
   }

   @Override
   public DependencyRepository removeRepository(final String url)
   {
      if (url != null)
      {
         MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
         Model pom = maven.getPOM();
         List<Repository> repos = pom.getRepositories();
         for (Repository repo : repos)
         {
            if (repo.getUrl().equals(url.trim()))
            {
               repos.remove(repo);
               maven.setPOM(pom);
               return new DependencyRepositoryImpl(repo.getId(), repo.getUrl());
            }
         }
      }
      return null;

   }

   @Override
   public List<Dependency> getDependenciesInScopes(final ScopeType... scopes)
   {
      List<Dependency> result = new ArrayList<Dependency>();
      List<Dependency> dependencies = getDependencies();
      for (Dependency dependency : dependencies)
      {
         for (ScopeType scope : scopes)
         {
            if ((dependency.getScopeTypeEnum() == null) || dependency.getScopeTypeEnum().equals(scope))
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
   public List<Dependency> getEffectiveDependenciesInScopes(final ScopeType... scopes)
   {
      List<Dependency> result = new ArrayList<Dependency>();
      List<Dependency> dependencies = getEffectiveDependencies();
      for (Dependency dependency : dependencies)
      {
         for (ScopeType scope : scopes)
         {
            if ((dependency.getScopeTypeEnum() == null) || dependency.getScopeTypeEnum().equals(scope))
            {
               dependency = resolveProperties(dependency);
               result.add(dependency);
               break;
            }
         }
      }
      return result;
   }
}

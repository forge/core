/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
import org.jboss.forge.project.dependencies.DependencyRepository;
import org.jboss.forge.project.dependencies.DependencyRepositoryImpl;
import org.jboss.forge.project.dependencies.DependencyResolver;
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
   public void addDependency(final Dependency dep)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      if (!hasDependency(dep))
      {
         Model pom = maven.getPOM();
         List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());
         dependencies.add(dep);
         pom.setDependencies(MavenDependencyAdapter.toMavenList(dependencies));
         maven.setPOM(pom);
         bus.enqueue(new AddedDependencies(project, dep));
      }
   }

   @Override
   public boolean hasDependency(final Dependency dep)
   {
      return hasEffectiveDependency(dep) || hasDirectDependency(dep);
   }

   @Override
   public boolean hasDirectDependency(final Dependency dependency)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(pom.getDependencies());

      for (Dependency dep : dependencies)
      {
         if (DependencyBuilder.areEquivalent(dependency, dep))
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
         if (DependencyBuilder.areEquivalent(dependency, dep))
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
      for (Dependency dependency : dependencies) {
         result.add(resolveProperties(dependency));
      }

      return result;
   }

   @Override
   public Dependency getDependency(final Dependency dep)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      List<Dependency> dependencies = MavenDependencyAdapter.fromMavenList(maven.getPartialProjectBuildingResult()
               .getProject().getDependencies());

      for (Dependency dependency : dependencies)
      {
         if (DependencyBuilder.areEquivalent(dependency, dep))
         {
            return resolveProperties(dependency);
         }
      }
      return null;
   }

   @Override
   public boolean hasEffectiveDependency(final Dependency dependency)
   {
      return getEffectiveDependency(dependency) != null;
   }

   @Override
   public Dependency getEffectiveDependency(final Dependency manDep)
   {
      for (Dependency dependency : getEffectiveDependencies())
      {
         if (DependencyBuilder.areEquivalent(dependency, manDep))
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
      List<Dependency> deps = MavenDependencyAdapter.fromAetherList(maven.getFullProjectBuildingResult()
               .getDependencyResolutionResult().getDependencies());

      List<Dependency> result = new ArrayList<Dependency>();
      for (Dependency dependency : deps) {
         result.add(resolveProperties(dependency));
      }

      return result;
   }

   @Override
   public void addManagedDependency(final Dependency manDep)
   {
      if (!hasEffectiveManagedDependency(manDep))
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
   public boolean hasEffectiveManagedDependency(final Dependency manDep)
   {
      return (getEffectiveManagedDependency(manDep) != null);
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
         if (DependencyBuilder.areEquivalent(managedDependency, manDep))
         {
            return resolveProperties(managedDependency);
         }
      }
      return null;
   }

   @Override
   public boolean hasManagedDependency(final Dependency managedDependency)
   {
      MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
      Model pom = maven.getPOM();
      DependencyManagement depMan = pom.getDependencyManagement();

      List<Dependency> managedDependencies = depMan != null ? MavenDependencyAdapter.fromMavenList(depMan
               .getDependencies()) : new ArrayList<Dependency>();

      for (Dependency manDep : managedDependencies)
      {
         if (DependencyBuilder.areEquivalent(managedDependency, manDep))
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
      for (Dependency dependency : managedDependencies) {
         managedDependencies.add(resolveProperties(dependency));
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
         if (DependencyBuilder.areEquivalent(managedDependency, manDep))
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
      Properties properties = mvn.getPartialProjectBuildingResult().getProject().getProperties();
      DependencyBuilder builder = DependencyBuilder.create(dependency);

      for (Entry<Object, Object> e : properties.entrySet())
      {
         String key = "\\$\\{" + e.getKey().toString() + "\\}";
         Object value = e.getValue();

         if (dependency.getGroupId() != null)
            builder.setGroupId(dependency.getGroupId().replaceAll(key, value.toString()));
         if (dependency.getArtifactId() != null)
            builder.setArtifactId(dependency.getArtifactId().replaceAll(key, value.toString()));
         if (dependency.getVersion() != null)
            builder.setVersion(dependency.getVersion().replaceAll(key, value.toString()));
         if (dependency.getClassifier() != null)
            builder.setClassifier(dependency.getClassifier().replaceAll(key, value.toString()));
         if (dependency.getPackagingType() != null)
            builder.setPackagingType(dependency.getPackagingType().replaceAll(key,
                     value.toString()));
         if (dependency.getScopeType() != null)
            builder.setScopeType(dependency.getScopeType().replaceAll(key, value.toString()));
      }

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
      List<Dependency> versions = resolver.resolveVersions(dep, getRepositories());
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
               repositories.remove(repo);
               maven.setPOM(pom);
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

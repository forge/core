/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.dependencies;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.resources.DependencyResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Singleton
public class DependencyResolver
{
   private final Instance<DependencyResolverProvider> providers;

   @Inject
   public DependencyResolver(final Instance<DependencyResolverProvider> providers)
   {
      this.providers = providers;
   }

   public List<DependencyResource> resolveArtifacts(final Dependency query)
   {
      for (DependencyResolverProvider p : providers)
      {
         List<DependencyResource> artifacts = p.resolveArtifacts(query);
         if ((artifacts != null) && !artifacts.isEmpty())
         {
            return artifacts;
         }
      }
      return new ArrayList<DependencyResource>();
   }

   public List<DependencyResource> resolveArtifacts(final Dependency query, final DependencyRepository repository)
   {
      for (DependencyResolverProvider p : providers)
      {
         List<DependencyResource> artifacts = p.resolveArtifacts(query, repository);
         if ((artifacts != null) && !artifacts.isEmpty())
         {
            return artifacts;
         }
      }
      return new ArrayList<DependencyResource>();
   }

   public List<DependencyResource> resolveArtifacts(final Dependency query,
            final List<DependencyRepository> repositories)
   {
      for (DependencyResolverProvider p : providers)
      {
         List<DependencyResource> artifacts = p.resolveArtifacts(query, repositories);
         if ((artifacts != null) && !artifacts.isEmpty())
         {
            return artifacts;
         }
      }
      return new ArrayList<DependencyResource>();
   }

   public List<DependencyResource> resolveDependencies(final Dependency query)
   {
      for (DependencyResolverProvider p : providers)
      {
         List<DependencyResource> artifacts = p.resolveDependencies(query);
         if ((artifacts != null) && !artifacts.isEmpty())
         {
            return artifacts;
         }
      }
      return new ArrayList<DependencyResource>();
   }

   public List<DependencyResource> resolveDependencies(final Dependency query,
            final DependencyRepository repository)
   {
      for (DependencyResolverProvider p : providers)
      {
         List<DependencyResource> artifacts = p.resolveDependencies(query, repository);
         if ((artifacts != null) && !artifacts.isEmpty())
         {
            return artifacts;
         }
      }
      return new ArrayList<DependencyResource>();
   }

   public List<DependencyResource> resolveDependencies(final Dependency query,
            final List<DependencyRepository> repositories)
   {
      for (DependencyResolverProvider p : providers)
      {
         List<DependencyResource> artifacts = p.resolveDependencies(query, repositories);
         if ((artifacts != null) && !artifacts.isEmpty())
         {
            return artifacts;
         }
      }
      return new ArrayList<DependencyResource>();
   }

   public DependencyMetadata resolveDependencyMetadata(final Dependency query)
   {
      for (DependencyResolverProvider p : providers)
      {
         DependencyMetadata meta = p.resolveDependencyMetadata(query);
         if (meta != null)
         {
            return meta;
         }
      }
      return null;
   }

   public DependencyMetadata resolveDependencyMetadata(final Dependency query, final DependencyRepository repository)
   {
      for (DependencyResolverProvider p : providers)
      {
         DependencyMetadata meta = p.resolveDependencyMetadata(query, repository);
         if (meta != null)
         {
            return meta;
         }
      }
      return null;
   }

   public DependencyMetadata resolveDependencyMetadata(final Dependency query,
            final List<DependencyRepository> repositories)
   {
      for (DependencyResolverProvider p : providers)
      {
         DependencyMetadata meta = p.resolveDependencyMetadata(query, repositories);
         if (meta != null)
         {
            return meta;
         }
      }
      return null;
   }

   /**
    * @deprecated Use {@link DependencyResolver#resolveVersions(DependencyQuery)}
    */
   public List<Dependency> resolveVersions(final Dependency query)
   {
      for (DependencyResolverProvider p : providers)
      {
         List<Dependency> artifacts = p.resolveVersions(query);
         if ((artifacts != null) && !artifacts.isEmpty())
         {
            return artifacts;
         }
      }
      return new ArrayList<Dependency>();
   }

   /**
    * @deprecated Use {@link DependencyResolver#resolveVersions(DependencyQuery)}
    */
   public List<Dependency> resolveVersions(final Dependency query, final DependencyRepository repository)
   {
      for (DependencyResolverProvider p : providers)
      {
         List<Dependency> artifacts = p.resolveVersions(query, repository);
         if ((artifacts != null) && !artifacts.isEmpty())
         {
            return artifacts;
         }
      }
      return new ArrayList<Dependency>();
   }

   /**
    * @deprecated Use {@link DependencyResolver#resolveVersions(DependencyQuery)}
    */
   public List<Dependency> resolveVersions(final Dependency query, final List<DependencyRepository> repositories)
   {
      for (DependencyResolverProvider p : providers)
      {
         List<Dependency> artifacts = p.resolveVersions(query, repositories);
         if ((artifacts != null) && !artifacts.isEmpty())
         {
            return artifacts;
         }
      }
      return new ArrayList<Dependency>();
   }

   /**
    * Resolve a set of {@link Dependency} versions matching the given query.
    */
   public List<Dependency> resolveVersions(final DependencyQuery query)
   {
      List<Dependency> deps = new ArrayList<Dependency>();
      DependencyFilter dependencyFilter = query.getDependencyFilter();
      Dependency dependency = query.getDependency();
      List<DependencyRepository> dependencyRepositories = query.getDependencyRepositories();
      for (DependencyResolverProvider p : providers)
      {
         List<Dependency> artifacts = p.resolveVersions(dependency, dependencyRepositories);
         if (artifacts != null)
         {
            for (Dependency artifact : artifacts)
            {
               if (dependencyFilter == null || dependencyFilter.accept(artifact))
               {
                  deps.add(artifact);
               }
            }
         }
      }
      return deps;
   }

}

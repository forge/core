/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

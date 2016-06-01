/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.dependencies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyMetadata;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class DependencyMetadataImpl implements DependencyMetadata
{

   private final Dependency dependency;
   private final List<DependencyRepository> repositories;
   private final List<Dependency> managedDependencies;
   private final List<Dependency> dependencies;

   public DependencyMetadataImpl(Dependency query, ArtifactDescriptorResult descriptor)
   {
      this.dependency = query;

      this.repositories = new ArrayList<>();
      for (RemoteRepository r : descriptor.getRepositories())
      {
         repositories.add(new DependencyRepository(r.getId(), r.getUrl()));
      }

      managedDependencies = new ArrayList<>();
      for (org.eclipse.aether.graph.Dependency d : descriptor.getManagedDependencies())
      {
         managedDependencies.add(convertToForge(d));
      }

      dependencies = new ArrayList<>();
      for (org.eclipse.aether.graph.Dependency d : descriptor.getDependencies())
      {
         dependencies.add(convertToForge(d));
      }
   }

   private Dependency convertToForge(org.eclipse.aether.graph.Dependency d)
   {
      Artifact a = d.getArtifact();
      Dependency dep = DependencyBuilder.create()
               .setArtifactId(a.getArtifactId())
               .setGroupId(a.getGroupId())
               .setVersion(a.getBaseVersion());
      return dep;
   }

   @Override
   public String toString()
   {
      return "[dependency=" + dependency + ", repositories=" + repositories
               + ", managedDependencies=" + managedDependencies + ", dependencies=" + dependencies + "]";
   }

   @Override
   public Dependency getDependency()
   {
      return dependency;
   }

   @Override
   public List<Dependency> getManagedDependencies()
   {
      return managedDependencies;
   }

   @Override
   public List<Dependency> getDependencies()
   {
      return dependencies;
   }

   @Override
   public List<DependencyRepository> getRepositories()
   {
      return repositories;
   }

}
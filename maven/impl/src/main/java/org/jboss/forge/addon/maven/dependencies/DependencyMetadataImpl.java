/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.dependencies;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyMetadata;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactDescriptorResult;

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

      this.repositories = new ArrayList<DependencyRepository>();
      for (RemoteRepository r : descriptor.getRepositories())
      {
         repositories.add(new DependencyRepository(r.getId(), r.getUrl()));
      }

      managedDependencies = new ArrayList<Dependency>();
      for (org.sonatype.aether.graph.Dependency d : descriptor.getManagedDependencies())
      {
         managedDependencies.add(convertToForge(d));
      }

      dependencies = new ArrayList<Dependency>();
      for (org.sonatype.aether.graph.Dependency d : descriptor.getDependencies())
      {
         dependencies.add(convertToForge(d));
      }
   }

   private Dependency convertToForge(org.sonatype.aether.graph.Dependency d)
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
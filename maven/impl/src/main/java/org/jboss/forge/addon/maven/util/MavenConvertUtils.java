/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyNodeBuilder;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;

public class MavenConvertUtils
{
   public static RemoteRepository convertToMavenRepo(final DependencyRepository repo, final Settings settings)
   {
      RemoteRepository.Builder remoteRepositoryBuilder = new RemoteRepository.Builder(repo.getId(), "default",
               repo.getUrl());
      Proxy activeProxy = settings.getActiveProxy();
      if (activeProxy != null)
      {
         Authentication auth = new AuthenticationBuilder().addUsername(activeProxy.getUsername())
                  .addPassword(activeProxy.getPassword()).build();
         remoteRepositoryBuilder.setProxy(new org.eclipse.aether.repository.Proxy(activeProxy.getProtocol(),
                  activeProxy
                           .getHost(), activeProxy.getPort(), auth));
      }
      return remoteRepositoryBuilder.build();
   }

   public static List<RemoteRepository> convertToMavenRepos(final List<DependencyRepository> repositories,
            final Settings settings)
   {
      List<RemoteRepository> remoteRepos = new ArrayList<>();
      if (repositories != null)
      {
         for (DependencyRepository deprep : repositories)
         {
            remoteRepos.add(convertToMavenRepo(deprep, settings));
         }
      }
      return remoteRepos;
   }

   public static Artifact coordinateToMavenArtifact(final Coordinate dep)
   {
      Artifact artifact = new DefaultArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getClassifier(),
               dep.getPackaging() == null ? "jar" : dep.getPackaging(), dep.getVersion());
      return artifact;
   }

   public static Dependency convertToDependency(ResourceFactory factory, DependencyNode node)
   {
      org.eclipse.aether.graph.Dependency artifactDependency = node.getDependency();
      Artifact artifact = artifactDependency.getArtifact();
      File file = artifact.getFile();

      @SuppressWarnings("unchecked")
      FileResource<?> artifactResource = factory.create(FileResource.class, file);

      Dependency d = DependencyBuilder.create().setArtifactId(artifact.getArtifactId())
               .setGroupId(artifact.getGroupId()).setVersion(artifact.getBaseVersion())
               .setPackaging(artifact.getExtension()).setArtifact(artifactResource)
               .setOptional(artifactDependency.isOptional())
               .setClassifier(artifact.getClassifier())
               .setScopeType(artifactDependency.getScope());
      return d;
   }

   public static DependencyNodeBuilder toDependencyNode(ResourceFactory factory,
            org.jboss.forge.addon.dependencies.DependencyNode parent, DependencyNode aetherNode)
   {
      DependencyNodeBuilder node = DependencyNodeBuilder.create(parent,
               MavenConvertUtils.convertToDependency(factory, aetherNode));
      for (DependencyNode childNode : aetherNode.getChildren())
      {
         node.getChildren().add(toDependencyNode(factory, node, childNode));
      }
      return node;
   }

}

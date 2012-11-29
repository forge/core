/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.container;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.jboss.forge.addon.dependency.Coordinate;
import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyRepository;
import org.jboss.forge.addon.dependency.builder.DependencyBuilder;
import org.jboss.forge.addon.dependency.builder.DependencyNodeBuilder;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

class MavenConvertUtils
{
   static RemoteRepository convertToMavenRepo(final DependencyRepository repo, final Settings settings)
   {
      RemoteRepository remoteRepository = new RemoteRepository(repo.getId(), "default", repo.getUrl());
      Proxy activeProxy = settings.getActiveProxy();
      if (activeProxy != null)
      {
         Authentication auth = new Authentication(activeProxy.getUsername(), activeProxy.getPassword());
         remoteRepository.setProxy(new org.sonatype.aether.repository.Proxy(activeProxy.getProtocol(), activeProxy
                  .getHost(), activeProxy.getPort(), auth));
      }
      return remoteRepository;
   }

   static List<RemoteRepository> convertToMavenRepos(final List<DependencyRepository> repositories,
            final Settings settings)
   {
      List<RemoteRepository> remoteRepos = new ArrayList<RemoteRepository>();
      for (DependencyRepository deprep : repositories)
      {
         remoteRepos.add(convertToMavenRepo(deprep, settings));
      }
      return remoteRepos;
   }

   static Artifact coordinateToMavenArtifact(final Coordinate dep)
   {
      Artifact artifact = new DefaultArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getClassifier(),
               dep.getPackaging() == null ? "jar" : dep.getPackaging(), dep.getVersion());
      return artifact;
   }

   static Dependency convertToDependency(DependencyNode node)
   {
      org.sonatype.aether.graph.Dependency artifactDependency = node.getDependency();
      Artifact artifact = artifactDependency.getArtifact();
      File file = artifact.getFile();

      Dependency d = DependencyBuilder.create().setArtifactId(artifact.getArtifactId())
               .setGroupId(artifact.getGroupId()).setVersion(artifact.getVersion())
               .setPackaging(artifact.getExtension()).setArtifact(file)
               .setOptional(artifactDependency.isOptional())
               .setClassifier(artifact.getClassifier())
               .setScopeType(artifactDependency.getScope());
      return d;
   }

   static DependencyNodeBuilder toDependencyNode(org.jboss.forge.addon.dependency.DependencyNode parent,
            DependencyNode aetherNode)
   {
      DependencyNodeBuilder node = DependencyNodeBuilder.create(parent,
               MavenConvertUtils.convertToDependency(aetherNode));
      for (DependencyNode childNode : aetherNode.getChildren())
      {
         node.getChildren().add(toDependencyNode(node, childNode));
      }
      return node;
   }

}

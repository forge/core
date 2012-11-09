/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Settings;
import org.jboss.forge.maven.dependency.Dependency;
import org.jboss.forge.maven.dependency.DependencyRepository;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

public class MavenConvertUtils
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

   static Artifact dependencyToMavenArtifact(final Dependency dep)
   {
      Artifact artifact = new DefaultArtifact(dep.getGroupId(), dep.getArtifactId(), dep.getClassifier(),
               dep.getPackagingType() == null ? "jar" : dep.getPackagingType(), dep.getVersion());
      return artifact;
   }
}

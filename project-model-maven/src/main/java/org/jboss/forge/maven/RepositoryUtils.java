/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.repository.layout.FlatRepositoryLayout;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.jboss.forge.parser.java.util.Strings;
import org.sonatype.aether.repository.Authentication;

/**
 * Repository Utils
 * 
 * @author George Gastaldi <gegastaldi@gmail.com>
 * 
 */
public final class RepositoryUtils
{
   private RepositoryUtils()
   {
   }

   public static org.sonatype.aether.repository.Proxy convertFromMavenProxy(org.apache.maven.settings.Proxy proxy)
   {
      org.sonatype.aether.repository.Proxy result = null;
      if (proxy != null)
      {
         Authentication auth = new Authentication(proxy.getUsername(), proxy.getPassword());
         result = new org.sonatype.aether.repository.Proxy(proxy.getProtocol(), proxy.getHost(), proxy.getPort(), auth);
      }
      return result;
   }

   public static ArtifactRepository convertFromMavenSettingsRepository(Repository repository)
   {
      MavenArtifactRepository result = new MavenArtifactRepository();
      result.setId(repository.getId());
      result.setUrl(repository.getUrl());

      String layout = repository.getLayout();
      if ("default".equals(layout))
         result.setLayout(new DefaultRepositoryLayout());
      else if ("flat".equals(layout))
         result.setLayout(new FlatRepositoryLayout());
      else if ("p2".equals(layout))
         result.setLayout(new P2ArtifactRepositoryLayout());

      RepositoryPolicy releases = repository.getReleases();
      if (releases != null)
         result.setReleaseUpdatePolicy(new ArtifactRepositoryPolicy(releases.isEnabled(), releases.getUpdatePolicy(),
                  releases.getChecksumPolicy()));

      RepositoryPolicy snapshots = repository.getSnapshots();
      if (snapshots != null)
         result.setSnapshotUpdatePolicy(new ArtifactRepositoryPolicy(snapshots.isEnabled(),
                  snapshots.getUpdatePolicy(),
                  snapshots.getChecksumPolicy()));

      return result;
   }

   public static ArtifactRepository toArtifactRepository(String id, String url, String layout,
            boolean containsReleases,
            boolean containsSnapshots)
   {
      MavenArtifactRepository result = new MavenArtifactRepository();
      result.setId(id);
      result.setUrl(url);

      if (Strings.isNullOrEmpty(layout) || "default".equals(layout))
      {
         result.setLayout(new DefaultRepositoryLayout());
      }
      else if ("flat".equals(layout))
      {
         result.setLayout(new FlatRepositoryLayout());
      }
      else if ("p2".equals(layout))
      {
         result.setLayout(new P2ArtifactRepositoryLayout());
      }
      result.setReleaseUpdatePolicy(new ArtifactRepositoryPolicy(containsReleases,
               ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER,
               ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN));
      result.setSnapshotUpdatePolicy(new ArtifactRepositoryPolicy(containsSnapshots,
               ArtifactRepositoryPolicy.UPDATE_POLICY_DAILY,
               ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN));
      return result;

   }

}

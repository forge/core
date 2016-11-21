/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.impl;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout2;

/**
 * Implements support for p2 repository layouts
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@SuppressWarnings("deprecation")
public class P2ArtifactRepositoryLayout
         implements ArtifactRepositoryLayout, ArtifactRepositoryLayout2
{
   public static final String ID = "p2";

   private static final ArtifactRepositoryPolicy DISABLED_POLICY =
            new ArtifactRepositoryPolicy(false, ArtifactRepositoryPolicy.UPDATE_POLICY_NEVER,
                     ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE);

   @Override
   public String pathOf(Artifact artifact)
   {
      return ".p2-ignore";
   }

   @Override
   public String pathOfLocalRepositoryMetadata(ArtifactMetadata metadata, ArtifactRepository repository)
   {
      return ".p2-ignore";
   }

   @Override
   public String pathOfRemoteRepositoryMetadata(ArtifactMetadata metadata)
   {
      return ".p2-ignore";
   }

   @Override
   public String getId()
   {
      return ID;
   }

   @Override
   public ArtifactRepository newMavenArtifactRepository(String id, String url, ArtifactRepositoryPolicy snapshots,
            ArtifactRepositoryPolicy releases)
   {
      return new MavenArtifactRepository(id, url, this, DISABLED_POLICY, DISABLED_POLICY);
   }

   @Override
   public String toString()
   {
      return getId();
   }
}

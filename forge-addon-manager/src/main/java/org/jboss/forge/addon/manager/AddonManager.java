/*
não * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.container.AddonEntry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;

/**
 * Installs addons into an {@link AddonRepository}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class AddonManager
{
   private AddonRepository repository;

   @Inject
   public AddonManager(AddonRepository repository)
   {
      this.repository = repository;
   }

   // XXX
   public void install(String coordinates)
   {
      MavenResolvedArtifact[] artifacts = null;
      artifacts = Maven.resolver().offline().resolve(coordinates).withTransitivity()
               .asResolvedArtifact();
      System.out.println("Transient files: ");
      list(artifacts);

      // MavenResolvedArtifact farArtifact = Maven.resolver().offline().resolve(coordinates).withoutTransitivity()
      // .asSingleResolvedArtifact();
      // repository.deploy(entry, farFile, dependencies)
   }

   private void list(MavenResolvedArtifact[] artifacts)
   {
      for (MavenResolvedArtifact mavenArtifact : artifacts)
      {
         if ("far".equals(mavenArtifact.getExtension()))
         {
            System.out.println("Depends on addon : " + mavenArtifact.getCoordinate() + " - " + mavenArtifact.asFile());
         }
         else
         {
            System.out.println(mavenArtifact.asFile());
         }
      }
   }

   public void install(AddonEntry entry, File farFile, File[] dependencies)
   {
   }

   public void remove(String coordinates)
   {
      AddonEntry entry = AddonEntry.fromCoordinates(coordinates);
      repository.remove(entry);
   }
}

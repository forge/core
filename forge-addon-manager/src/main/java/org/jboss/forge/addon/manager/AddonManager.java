/*
não * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.container.AddonEntry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;
import org.jboss.shrinkwrap.resolver.api.maven.filter.MavenResolutionFilter;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.MavenResolutionStrategy;

/**
 * Installs addons into an {@link AddonRepository}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class AddonManager
{
   private AddonRepository repository;
   private MavenResolutionStrategy strategy = new MavenResolutionStrategy()
   {

      @Override
      public MavenResolutionFilter[] getPreResolutionFilters()
      {
         return Arrays.asList(new MavenResolutionFilter()
         {
            @Override
            public boolean accepts(MavenDependency dependency, List<MavenDependency> dependenciesForResolution)
            {
               // TODO Auto-generated method stub
               return false;
            }
         }).toArray(new MavenResolutionFilter[] {});
      }

      @Override
      public MavenResolutionFilter[] getResolutionFilters()
      {
         return Arrays.asList(new MavenResolutionFilter()
         {
            @Override
            public boolean accepts(MavenDependency dependency, List<MavenDependency> dependenciesForResolution)
            {
               // TODO Auto-generated method stub
               return false;
            }
         }).toArray(new MavenResolutionFilter[] {});
      }
   };

   @Inject
   public AddonManager(AddonRepository repository)
   {
      this.repository = repository;
   }

   public boolean install(AddonEntry entry)
   {
      String coordinates = toMavenCoordinates(entry);
      File far = Maven.resolver().offline().resolve(coordinates).withoutTransitivity().asSingleFile();
      File[] dependencies = Maven.resolver().offline().resolve(coordinates).using(strategy).asFile();
      return install(entry, far, dependencies);
   }

   public String toMavenCoordinates(AddonEntry entry)
   {
      return entry.toCoordinates().replaceAll("([^:]+):([^:]+):([^:]+)", "$1:$2:far:$3");
   }

   public boolean install(AddonEntry entry, File farFile, File[] dependencies)
   {
      repository.deploy(entry, farFile, dependencies);
      return repository.enable(entry);
   }

   public boolean remove(AddonEntry entry)
   {
      return repository.disable(entry);
   }
}

/*
não * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.dependency.Dependency;
import org.jboss.forge.addon.dependency.DependencyNode;
import org.jboss.forge.addon.dependency.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependency.spi.DependencyResolver;
import org.jboss.forge.container.AddonEntry;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.container.services.Remote;

/**
 * Installs addons into an {@link AddonRepository}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Remote
public class AddonManager
{
   private AddonRepository repository;
   private DependencyResolver resolver;

   @Inject
   public AddonManager(AddonRepository repository, DependencyResolver resolver)
   {
      this.repository = repository;
      this.resolver = resolver;
   }

   public boolean install(AddonEntry entry)
   {
      String coordinates = toMavenCoordinates(entry);
      File far = resolver.resolveArtifact(DependencyQueryBuilder.create(toMavenCoordinates(entry)));
      DependencyNode root = resolver.resolveDependencyHierarchy(coordinates);
      File[] dependencies = toDependencies(resolver.resolveAddonDependencies(coordinates));
      return install(entry, far, dependencies);
   }

   private File[] toDependencies(List<Dependency> dependencies)
   {
      List<File> result = new ArrayList<File>();
      for (Dependency dependency : dependencies)
      {
         result.add(dependency.getArtifact());
      }
      return result.toArray(new File[dependencies.size()]);
   }

   public String toMavenCoordinates(AddonEntry entry)
   {
      return entry.getName() + ":jar:forge-addon:" + entry.getVersion();
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

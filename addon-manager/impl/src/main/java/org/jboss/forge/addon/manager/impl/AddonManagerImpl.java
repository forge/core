/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl;

import javax.inject.Inject;

import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.dependencies.AddonDependencyResolver;
import org.jboss.forge.dependencies.DependencyNode;
import org.jboss.forge.dependencies.builder.DependencyQueryBuilder;

/**
 * Installs addons into an {@link AddonRepository}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class AddonManagerImpl implements AddonManager
{
   private AddonRepository repository;
   private AddonDependencyResolver resolver;

   @Inject
   public AddonManagerImpl(AddonRepository repository, AddonDependencyResolver resolver)
   {
      this.repository = repository;
      this.resolver = resolver;
   }

   @Override
   public InstallRequest install(AddonId addonId)
   {
      String coordinates = addonId.getName() + ":jar:forge-addon:" + addonId.getVersion();
      DependencyNode requestedAddonNode = resolver.resolveAddonDependencyHierarchy(DependencyQueryBuilder
               .create(coordinates));

      return new InstallRequestImpl(this, repository, requestedAddonNode);
   }

   @Override
   public boolean remove(AddonId entry)
   {
      return repository.disable(entry);
   }
}

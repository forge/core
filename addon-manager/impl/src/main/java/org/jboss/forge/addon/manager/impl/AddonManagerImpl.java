/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl;

import javax.inject.Inject;

import org.jboss.forge.addon.dependency.DependencyNode;
import org.jboss.forge.addon.dependency.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependency.spi.DependencyResolver;
import org.jboss.forge.addon.manager.AddonManager;
import org.jboss.forge.addon.manager.InstallRequest;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.AddonRepository;
import org.jboss.forge.container.services.Exported;

/**
 * Installs addons into an {@link AddonRepository}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
@Exported
public class AddonManagerImpl implements AddonManager
{
   private AddonRepository repository;
   private DependencyResolver resolver;

   @Inject
   public AddonManagerImpl(AddonRepository repository, DependencyResolver resolver)
   {
      this.repository = repository;
      this.resolver = resolver;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.addon.manager.impl.AddonManager#install(org.jboss.forge.container.AddonId)
    */
   @Override
   public InstallRequest install(AddonId addonId)
   {
      String coordinates = addonId.getName() + ":jar:forge-addon:" + addonId.getVersion();
      DependencyNode requestedAddonNode = resolver.resolveDependencyHierarchy(DependencyQueryBuilder
               .create(coordinates));

      return new InstallRequestImpl(this, repository, requestedAddonNode);
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.addon.manager.impl.AddonManager#remove(org.jboss.forge.container.AddonId)
    */
   @Override
   public boolean remove(AddonId entry)
   {
      return repository.disable(entry);
   }
}

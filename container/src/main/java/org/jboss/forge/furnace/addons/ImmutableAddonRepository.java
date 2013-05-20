/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.furnace.addons;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.furnace.repositories.AddonRepository;

/**
 * An immutable {@link AddonRepository} implementation that delegates to a wrapped instance.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ImmutableAddonRepository implements AddonRepository
{
   private AddonRepository delegate;

   public ImmutableAddonRepository(AddonRepository delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public File getAddonBaseDir(AddonId addonId)
   {
      return delegate.getAddonBaseDir(addonId);
   }

   @Override
   public Set<AddonDependencyEntry> getAddonDependencies(AddonId addonId)
   {
      return delegate.getAddonDependencies(addonId);
   }

   @Override
   public File getAddonDescriptor(AddonId addonId)
   {
      return delegate.getAddonDescriptor(addonId);
   }

   @Override
   public List<File> getAddonResources(AddonId addonId)
   {
      return delegate.getAddonResources(addonId);
   }

   @Override
   public File getRootDirectory()
   {
      return delegate.getRootDirectory();
   }

   @Override
   public boolean isDeployed(AddonId addonId)
   {
      return delegate.isDeployed(addonId);
   }

   @Override
   public boolean isEnabled(AddonId addonId)
   {
      return delegate.isEnabled(addonId);
   }

   @Override
   public List<AddonId> listEnabled()
   {
      return delegate.listEnabled();
   }

   @Override
   public List<AddonId> listEnabledCompatibleWithVersion(String version)
   {
      return delegate.listEnabledCompatibleWithVersion(version);
   }

   @Override
   public Date getLastModified()
   {
      return delegate.getLastModified();
   }

   @Override
   public int getVersion()
   {
      return delegate.getVersion();
   }

   @Override
   public String toString()
   {
      return delegate.toString() + " (Immutable)";
   }
}

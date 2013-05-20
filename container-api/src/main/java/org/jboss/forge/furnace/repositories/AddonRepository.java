/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.repositories;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.furnace.addons.AddonId;

/**
 * Used to perform Addon installation/registration operations. May be obtained using CDI injection:
 * <p>
 * <code>@{@link Inject} private {@link AddonRepository} repository;</code>
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface AddonRepository
{
   public File getAddonBaseDir(AddonId addon);

   public Set<AddonDependencyEntry> getAddonDependencies(AddonId addon);

   public File getAddonDescriptor(AddonId addon);

   public List<File> getAddonResources(AddonId addon);

   public File getRootDirectory();

   public boolean isDeployed(AddonId addon);

   public boolean isEnabled(final AddonId addon);

   public List<AddonId> listEnabled();

   public List<AddonId> listEnabledCompatibleWithVersion(final String version);

   public Date getLastModified();

   public int getVersion();
}

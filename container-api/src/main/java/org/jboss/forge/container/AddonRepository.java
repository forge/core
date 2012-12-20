/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

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
   public boolean deploy(AddonId addon, List<AddonDependency> dependencies, List<File> resourceJars);

   public boolean disable(final AddonId addon);

   public boolean enable(AddonId addon);

   public File getAddonBaseDir(AddonId addon);

   public Set<AddonDependency> getAddonDependencies(AddonId addon);

   public File getAddonDescriptor(AddonId addon);

   public List<File> getAddonResources(AddonId addon);

   public File getRepositoryDirectory();

   public File getRepositoryRegistryFile();

   public boolean isDeployed(AddonId addon);

   public boolean isEnabled(final AddonId addon);

   public List<AddonId> listEnabled();

   public List<AddonId> listEnabledCompatibleWithVersion(final String version);

   public boolean undeploy(AddonId addonEntry);

}

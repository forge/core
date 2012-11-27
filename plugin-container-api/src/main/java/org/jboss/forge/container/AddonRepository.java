/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.io.File;
import java.util.List;

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
   public boolean deploy(AddonEntry entry, File farFile, File... dependencies);

   public File getAddonBaseDir(AddonEntry found);

   public List<AddonDependency> getAddonDependencies(AddonEntry addon);

   public File getAddonDescriptor(AddonEntry addon);

   public File getAddonResourceDir(AddonEntry found);

   public List<File> getAddonResources(AddonEntry found);

   public File getRegistryFile();

   public File getRepositoryDirectory();

   public boolean isEnabled(final AddonEntry addon);

   public boolean enable(AddonEntry addon);

   public List<AddonEntry> listEnabledCompatibleWithVersion(final String version);

   public List<AddonEntry> listEnabled();

   public boolean disable(final AddonEntry addon);
}

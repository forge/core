/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.File;

import javax.inject.Inject;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.util.Assert;

/**
 * Creates {@link Shell} instances
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellFactoryImpl implements ShellFactory
{
   private Furnace furnace;
   private final ResourceFactory resourceFactory;
   private final AddonRegistry addonRegistry;

   @Inject
   public ShellFactoryImpl(Furnace furnace, AddonRegistry addonRegistry, ResourceFactory resourceFactory)
   {
      super();
      this.furnace = furnace;
      this.addonRegistry = addonRegistry;
      this.resourceFactory = resourceFactory;
   }

   @Override
   public Shell createShell(File initialSelection, Settings settings)
   {
      Assert.notNull(settings, "Settings cannot be null");
      Resource<?> initialResource = resourceFactory.create(initialSelection);
      return new ShellImpl(furnace, initialResource, settings, addonRegistry);
   }

   @Override
   public Shell createShell(Resource<?> intialSelection, Settings settings)
   {
      Assert.notNull(settings, "Settings cannot be null");
      return new ShellImpl(furnace, intialSelection, settings, addonRegistry);
   }

}

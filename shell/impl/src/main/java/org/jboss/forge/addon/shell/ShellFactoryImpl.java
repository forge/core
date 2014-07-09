/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.util.Assert;

/**
 * Creates {@link Shell} instances
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellFactoryImpl implements ShellFactory
{
   private final ResourceFactory resourceFactory;
   private final AddonRegistry addonRegistry;

   @Inject
   public ShellFactoryImpl(AddonRegistry addonRegistry, ResourceFactory resourceFactory)
   {
      super();
      this.addonRegistry = addonRegistry;
      this.resourceFactory = resourceFactory;
   }

   @Override
   public Shell createShell(File initialSelection, Settings settings)
   {
      Assert.notNull(settings, "Settings cannot be null");
      Resource<?> initialResource = resourceFactory.create(initialSelection);
      return new ShellImpl(initialResource, settings, addonRegistry);
   }

   @Override
   public Shell createShell(Resource<?> intialSelection, Settings settings)
   {
      Assert.notNull(settings, "Settings cannot be null");
      return new ShellImpl(intialSelection, settings, addonRegistry);
   }

}

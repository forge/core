/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell;

import java.io.File;

import javax.inject.Inject;

import org.jboss.aesh.console.helper.ManProvider;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.forge.addon.resource.FileResource;
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
   private final CommandManager commandManager;
   private final ManProvider manProvider;

   @Inject
   public ShellFactoryImpl(AddonRegistry addonRegistry, CommandManager commandManager,
                           ResourceFactory resourceFactory, ManProvider manProvider)
   {
      super();
      this.addonRegistry = addonRegistry;
      this.commandManager = commandManager;
      this.resourceFactory = resourceFactory;
      this.manProvider = manProvider;
   }

   @Override
   public Shell createShell(File initialSelection, Settings settings)
   {
      Assert.notNull(settings, "Settings cannot be null");
      FileResource<?> initialResource = resourceFactory.create(initialSelection).reify(FileResource.class);
      return new ShellImpl(initialResource, settings, commandManager, addonRegistry, manProvider);
   }

}

/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell;

import javax.inject.Inject;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.util.Assert;

/**
 * Creates {@link Shell} instances
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellFactoryImpl implements ShellFactory
{

   @Inject
   private AddonRegistry addonRegistry;

   @Override
   public Shell createShell(Settings settings)
   {
      Assert.notNull(settings, "Settings cannot be null");
      return new ShellImpl(addonRegistry, settings);
   }

}

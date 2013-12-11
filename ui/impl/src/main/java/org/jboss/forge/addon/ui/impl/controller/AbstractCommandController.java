/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.spi.UIRuntime;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractCommandController implements CommandController
{
   protected final AddonRegistry addonRegistry;
   protected final UIRuntime runtime;
   protected final UIContext context;
   protected final UICommand initialCommand;

   protected AbstractCommandController(AddonRegistry addonRegistry, UIRuntime contextFactory,
            UICommand initialCommand)
   {
      this.addonRegistry = addonRegistry;
      this.runtime = contextFactory;
      this.context = contextFactory.createUIContext();
      this.initialCommand = initialCommand;
   }
}

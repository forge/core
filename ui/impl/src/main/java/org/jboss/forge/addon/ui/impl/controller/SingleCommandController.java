/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.controller;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
abstract class SingleCommandController extends AbstractCommandController
{

   public SingleCommandController(AddonRegistry addonRegistry, UICommand initialCommand)
   {
      super(addonRegistry, initialCommand);
   }

}

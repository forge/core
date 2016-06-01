/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * Encapsulates the {@link UICommand} creation.
 * 
 * This interface should only be used in {@link CommandController} implementations
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface NavigationResultEntry
{
   /**
    * Returns a Command associated with this {@link NavigationResultEntry}
    * 
    * @param addonRegistry the {@link AddonRegistry} instance of this
    * @param context the current {@link UIContext}
    * @return command instance, never null
    */
   UICommand getCommand(AddonRegistry addonRegistry, UIContext context);
}

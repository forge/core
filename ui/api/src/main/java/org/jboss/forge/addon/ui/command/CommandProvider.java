/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.command;

import org.jboss.forge.furnace.addons.AddonId;

/**
 * Responsible for providing {@link UICommand} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CommandProvider
{
   /**
    * Get all {@link UICommand} instances from this {@link CommandProvider}.
    */
   Iterable<UICommand> getCommands();
   
   /**
    * Clear all the cached data for the given addon. 
    * Mainly usable for testing purposes, when an addon is manually undeployed but it's annotated commands stay cached.
    */
   void addonUndeployed(AddonId addonId);
}

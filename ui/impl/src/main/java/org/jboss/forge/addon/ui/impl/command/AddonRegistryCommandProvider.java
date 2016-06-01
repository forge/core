/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.command;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.CommandProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * Implementation of {@link CommandProvider} that uses the {@link AddonRegistry}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonRegistryCommandProvider implements CommandProvider
{
   @Inject
   private AddonRegistry registry;

   @Override
   public Iterable<UICommand> getCommands()
   {
      return registry.getServices(UICommand.class);
   }

   @Override
   public void addonUndeployed(AddonId addonId)
   {
      //don't do anything because this provider does not store any cache data
   }

}

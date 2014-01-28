/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.command;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.CommandFactory;
import org.jboss.forge.addon.ui.command.CommandProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.Imported;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CommandFactoryImpl implements CommandFactory
{
   @Inject
   private AddonRegistry registry;

   @Override
   public Iterable<UICommand> getCommands()
   {
      Set<UICommand> result = new HashSet<>();
      synchronized (this)
      {
         Imported<CommandProvider> instances = registry.getServices(CommandProvider.class);
         for (CommandProvider provider : instances)
         {
            for (UICommand command : provider.getCommands())
            {
               if (!(command instanceof UIWizardStep))
               {
                  result.add(command);
               }
            }
            instances.release(provider);
         }
      }
      return result;
   }

}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.command;

import java.util.Set;

import org.jboss.forge.addon.ui.context.UIContext;

/**
 * Responsible for producing {@link UICommand} objects from all registered {@link CommandProvider} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CommandFactory
{
   /**
    * Get all {@link UICommand} instances from all available {@link CommandProvider} services.
    * The underlying collection of UICommands is very likely be cached, and thus invoking operations
    * directly on them is not recommended for safety.
    * 
    * Consecutive invocations may be performed on the same UICommand instance, instead of different instances.
    * 
    * It is recommended to create a new UICommand when required. This can be done through the
    * {@link #getNewCommandByName(UIContext, String)} method.
    */
   Iterable<UICommand> getCommands();

   /**
    * Get all enabled command names enabled for the given {@link UIContext}
    */
   Set<String> getEnabledCommandNames(UIContext context);

   /**
    * Get all command names enabled for the given {@link UIContext}
    */
   Set<String> getCommandNames(UIContext context);

   /**
    * Get a {@link UICommand} instance given its name in the given {@link UIContext}
    */
   UICommand getCommandByName(UIContext context, String name);

   /**
    * Return the {@link UICommand} name for a given {@link UIContext}
    */
   String getCommandName(UIContext context, UICommand cmd);

   /**
    * Get an un-cached {@link UICommand} instance given its name in the given {@link UIContext}
    */
   UICommand getNewCommandByName(UIContext context, String name);
}

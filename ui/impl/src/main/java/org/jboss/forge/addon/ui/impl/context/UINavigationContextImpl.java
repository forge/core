/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.context;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Results;

/**
 * Implementation of the {@link UINavigationContext} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UINavigationContextImpl implements UINavigationContext
{

   private final UIContext context;
   private final UICommand initialCommand;
   private final UICommand currentCommand;

   public UINavigationContextImpl(UIContext context, UICommand initialCommand, UICommand currentCommand)
   {
      this.context = context;
      this.initialCommand = initialCommand;
      this.currentCommand = currentCommand;
   }

   @Override
   public UIContext getUIContext()
   {
      return context;
   }

   @Override
   @SuppressWarnings("unchecked")
   public NavigationResult navigateTo(Class<? extends UICommand> next, Class<? extends UICommand>... additional)
   {
      return Results.navigateTo(next, additional);
   }

   @Override
   public UICommand getInitialCommand()
   {
      return initialCommand;
   }

   @Override
   public UICommand getCurrentCommand()
   {
      return currentCommand;
   }
}

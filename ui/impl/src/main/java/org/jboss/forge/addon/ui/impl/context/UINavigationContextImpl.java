/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.context;

import org.jboss.forge.addon.ui.UICommand;
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

   public UINavigationContextImpl(UIContext context)
   {
      this.context = context;
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
}

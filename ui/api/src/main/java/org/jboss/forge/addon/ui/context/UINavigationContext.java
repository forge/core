/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.context;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.result.NavigationResult;

/**
 * A {@link UINavigationContext} is created when the navigation phase is requested
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UINavigationContext extends UIContextProvider
{
   @SuppressWarnings("unchecked")
   NavigationResult navigateTo(Class<? extends UICommand> next,
            Class<? extends UICommand>... additional);

   /**
    * The initial command that originated this navigation flow
    */
   UICommand getInitialCommand();

   /**
    * The current command in this navigation flow
    */
   UICommand getCurrentCommand();
}

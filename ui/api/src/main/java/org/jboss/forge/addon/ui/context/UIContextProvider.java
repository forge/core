/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.context;

import org.jboss.forge.addon.ui.command.UICommand;

/**
 * Provides a {@link UIContext} object
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface UIContextProvider
{

   /**
    * Returns the {@link UIContext} that is shared through all the {@link UICommand} instances in an interaction with
    * the user.
    */
   public abstract UIContext getUIContext();

}
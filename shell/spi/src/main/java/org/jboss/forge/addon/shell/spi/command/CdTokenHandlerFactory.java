/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.spi.command;

import java.util.List;

import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * Extension point for the 'cd' command to register and retrieve {@link CdTokenHandler} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface CdTokenHandlerFactory
{
   /**
    * Get all currently registered {@link CdTokenHandler} instances.
    */
   List<CdTokenHandler> getHandlers();

   /**
    * Register a new {@link CdTokenHandler} instance.
    */
   ListenerRegistration<CdTokenHandler> addTokenHandler(CdTokenHandler handler);
}

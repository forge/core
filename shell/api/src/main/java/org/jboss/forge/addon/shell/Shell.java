/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.command.CommandExecutionListener;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * The command line shell.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface Shell extends UIProvider, AutoCloseable
{
   /**
    * Get the native {@link AeshConsole} object.
    */
   AeshConsole getConsole();

   /**
    * Sets the current working directory
    * 
    * @param resource should be a {@link FileResource}
    * @throws IllegalArgumentException if resource is null
    */
   void setCurrentResource(Resource<?> resource);

   /**
    * Returns the current working directory.
    */
   Resource<?> getCurrentResource();

   /**
    * Add a {@link CommandExecutionListener}, returning the {@link ListenerRegistration} with which it may subsequently
    * be removed.
    */
   ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(CommandExecutionListener listener);

   /**
    * Add a {@link CommandNotFoundListener}, returning the {@link ListenerRegistration} with which it may subsequently
    * be removed.
    */
   ListenerRegistration<CommandNotFoundListener> addCommandNotFoundListener(CommandNotFoundListener listener);
}

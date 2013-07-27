/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.aesh.console.Console;
import org.jboss.forge.addon.shell.spi.CommandExecutionListener;
import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.spi.ListenerRegistration;

/**
 * The command line shell.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface ForgeShell
{
   /**
    * Set InputStream
    */
   public void setInputStream(InputStream is);
   
   /**
    * Set StdOut
    */
   public void setStdOut(OutputStream os);
   
   /**
    * Set StdErr
    */
   public void setStdErr(OutputStream os);
   
   /**
    * Start the shell.
    */
   public void startShell() throws Exception;

   /**
    * Get the current prompt.
    */
   public String getPrompt();

   /**
    * Get the native {@link Console} object.
    */
   public Console getConsole();

   /**
    * Stop the shell.
    */
   public void stopShell() throws IOException;

   /**
    * Add a {@link CommandExecutionListener}, returning the {@link ListenerRegistration} with which it may subsequently
    * be removed.
    */
   public ListenerRegistration<CommandExecutionListener> addCommandExecutionListener(CommandExecutionListener listener);
}

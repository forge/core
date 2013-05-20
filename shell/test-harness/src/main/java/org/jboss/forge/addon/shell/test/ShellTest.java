/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.test;

import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.services.Exported;

/**
 * Utility for interacting with the Forge shell in a synchronous manner.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface ShellTest
{
   /**
    * Execute the given line and return the {@link Result}. Clears STDOUT and STDERR before execution.
    */
   public Result execute(String line);

   /**
    * Execute the given line and return the {@link Result}. Fail if not complete within the given quantity of
    * {@link TimeUnit}. Clears STDOUT and STDERR before execution.
    */
   public Result execute(String line, int quantity, TimeUnit unit);

   /**
    * Clear and wait for the next write to STDOUT. Send the provided line to STDIN. Fail if no write occurs within the
    * given quantity of {@link TimeUnit}
    */
   public void waitForStdOut(String input, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * Clear and wait for the next write to STDOUT. Send the provided line to STDIN. Fail if no write occurs within the
    * given quantity of {@link TimeUnit}
    */
   public void waitForStdErr(String input, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * Get the STDIN {@link OutputStream} for writing.
    */
   public OutputStream getStdIn();

   /**
    * Get the current contents of STDOUT since the last time it was cleared.
    */
   public String getStdOut();

   /**
    * Get the current contents of STDERR since the last time it was cleared.
    */
   public String getStdErr();
}

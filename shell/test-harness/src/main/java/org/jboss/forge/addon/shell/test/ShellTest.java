/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.aesh.console.Buffer;
import org.jboss.aesh.console.Console;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.ui.result.Result;

/**
 * Utility for interacting with the Forge {@link Shell} in an asynchronous manner, providing methods to perform blocking
 * writes, reads, and other test functions.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ShellTest extends AutoCloseable
{
   /**
    * Get the {@link Console} buffer object.
    */
   String getBuffer();

   /**
    * Get the underlying test {@link Shell} object.
    */
   Shell getShell();

   /**
    * Execute the given line without waiting for it to run to completion.
    */
   void execute(String line);

   /**
    * Execute the given line and return the {@link Result}. Fail if not complete within the given quantity of
    * {@link TimeUnit}. (Appends the newline character to the given input if necessary, and calls
    * {@link OutputStream#flush()} on {@link #getStdIn()}).
    * 
    * @throws TimeoutException if the given command was not executed successfully within the allotted timeout.
    */
   Result execute(String line, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * Wait for the console {@link Buffer} to be updated after the given task is executed.
    * 
    * @throws TimeoutException if the timeout is reached without detecting a buffer value change.
    */
   void waitForBufferChanged(Callable<?> task, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * Wait for the console {@link Buffer} to be updated after the given task is executed, and assert that it matches the
    * given {@link String}.
    * 
    * @throws TimeoutException if the timeout is reached without detecting the appropriate value.
    */
   void waitForBufferValue(Callable<?> task, String expected, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * Clear and wait for the next write to STDOUT. Send the provided line to STDIN.
    * 
    * @throws TimeoutException if the timeout is reached without detecting the expected write to STDOUT.
    */
   void waitForStdOutChanged(String input, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * lear and wait for the next write to STDOUT. Execute the given {@link Callable} task, waiting for STDOUT and
    * returning the resultant output.
    * 
    * @throws TimeoutException if the timeout is reached without detecting the expected write to STDOUT.
    */
   String waitForStdOutChanged(Callable<?> task, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * Execute the given {@link Callable} task, waiting for STDOUT to be updated, and assert that it matches the given
    * value.
    * 
    * @throws TimeoutException if the timeout is reached without detecting the expected write to STDOUT.
    */
   void waitForStdOutValue(Callable<Void> task, String expected, int timeout, TimeUnit unit) throws TimeoutException;

   /**
    * Clear and wait for the next write to STDOUT. Send the provided line to STDIN. Fail if no write occurs within the
    * given quantity of {@link TimeUnit}
    * 
    * @throws TimeoutException if the timeout is reached without detecting a write to STDERR.
    */
   void waitForStdErrChanged(String input, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * Wait for STDOUT to write the expected value. Does not clear STDOUT before waiting.
    * 
    * @throws TimeoutException if the timeout is reached without detecting the expected write to STDOUT.
    */
   void waitForStdOutValue(String expected, int timeout, TimeUnit unit) throws TimeoutException;

   /**
    * Wait for STDERR to write the expected value. Does not clear STDERR before waiting.
    * 
    * @throws TimeoutException if the timeout is reached without detecting the expected write to STDERR.
    */
   void waitForStdErrValue(String expected, int timeout, TimeUnit unit) throws TimeoutException;

   /**
    * Clear STDERR, execute the given {@link Callable} task, waiting for STDERR and returning the resultant output.
    * 
    * @throws TimeoutException if the timeout is reached without detecting a write to STDERR.
    */
   String waitForStdErrChanged(Callable<?> callable, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * Clear STDERR, execute the given {@link Callable} task, waiting for STDERR to be updated, and assert that it
    * matches the given value.
    * 
    * @throws TimeoutException if the timeout is reached without detecting the expected write to STDERR.
    */
   void waitForStdErrValue(Callable<Void> task, String expected, int timeout, TimeUnit unit) throws TimeoutException;

   /**
    * Get the STDIN {@link OutputStream} for writing.
    */
   OutputStream getStdIn();

   /**
    * Get the current contents of STDOUT since the last time it was cleared.
    */
   String getStdOut();

   /**
    * Get the current contents of STDERR since the last time it was cleared.
    */
   String getStdErr();

   /**
    * Write to STDIN. (Does not call {@link OutputStream#flush()} on {@link #getStdIn()}.)
    */
   void write(String string) throws IOException;

   /**
    * Initiate completion by sending the appropriate signal or character sequence to the {@link Shell}.
    */
   void sendCompletionSignal() throws IOException;

   /**
    * Clear the screen and reset the buffer.
    */
   void clearScreen() throws IOException;

   /**
    * Clear STDOUT, then write the given string to the buffer and assert that the full buffer is equal to the expected
    * content. Return the contents of STDOUT since the given text was written.
    * 
    * @throws TimeoutException if the buffer did not match STDOUT within the given timeout.
    */
   String waitForCompletion(String expected, String write, int quantity, TimeUnit unit) throws TimeoutException;

   /**
    * Clear STDOUT, then send the completion signal. Return the contents of STDOUT once completion has finished and the
    * buffer is re-written to STDOUT.
    * 
    * @throws TimeoutException if the buffer did not match STDOUT within the given timeout.
    */
   String waitForCompletion(int quantity, TimeUnit unit) throws TimeoutException;
}

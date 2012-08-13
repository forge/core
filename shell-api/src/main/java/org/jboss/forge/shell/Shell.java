/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.integration.BufferManager;
import org.jboss.forge.shell.integration.KeyListener;
import org.jboss.forge.shell.plugins.RequiresResource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Mike Brock
 */
public interface Shell extends ShellPrintWriter, ShellPrompt, ShellHistory
{
   /**
    * Return the current working directory resource of the shell. Start with {@link #getCurrentResource()} and move up
    * the hierarchy until a {@link DirectoryResource} is found. (This value may change through execution of plug-ins or
    * other operations.)
    */
   DirectoryResource getCurrentDirectory();

   /**
    * Return the current working {@link Resource} of the shell. (This value may change through execution of plug-ins or
    * other operations.)
    * 
    * TODO implement ResourceBag for multiple resources
    */
   Resource<?> getCurrentResource();

   /**
    * Return the type of the {@link Resource} on which the Shell is currently operating.
    * 
    * @see {@link RequiresResource}
    */
   Class<? extends Resource<?>> getCurrentResourceScope();

   /**
    * Set the {@link Resource} on which the shell should operate.
    * <p>
    * Note: This may change the current {@link Shell#getCurrentDirectory()}
    */
   void setCurrentResource(Resource<?> resource);

   /**
    * Return the file-system directory currently in use as the configuration directory. (Usually "~/.forge/")
    */
   DirectoryResource getConfigDir();

   /**
    * Return the {@link Project} on which this shell instance is currently operating.
    */
   Project getCurrentProject();

   /**
    * Return true if this shell is currently set to handle exceptions. If exception handling is enabled, this shell will
    * capture all exceptions thrown during command execution; if disabled, exceptions will not be caught, and must be
    * caught outside of the shell to avoid termination.
    */
   boolean isExceptionHandlingEnabled();

   /**
    * Toggle exception handling.
    * 
    * @see {@link #isExceptionHandlingEnabled()}
    */
   void setExceptionHandlingEnabled(boolean enabled);

   /**
    * Return true if this {@link Shell} is currently set to accept prompt defaults automatically.
    */
   boolean isAcceptDefaults();

   /**
    * Set this {@link Shell} to accept prompt defaults automatically.
    */
   void setAcceptDefaults(boolean accept);

   /**
    * Return true if this shell is currently running in pretend mode.
    * <p/>
    * Modifications to files made while running in pretend mode are made in a temporary directory, and the output is
    * produced as a Diff that can then be applied to the project.
    */
   boolean isPretend();

   /**
    * Return true if this shell is currently running in verbose mode.
    */
   boolean isVerbose();

   /**
    * Toggle verbose mode.
    */
   void setVerbose(boolean verbose);

   /**
    * Return true if this shell is currently keeping a record of command history.
    */
   boolean isHistoryEnabled();

   /**
    * Toggle whether or not this shell should keep a record of command history.
    */
   void setHistoryEnabled(boolean history);

   /**
    * Write output to the console, only if {@link Shell#isVerbose()} <code> == true</code>.
    */
   void printlnVerbose(String output);

   /**
    * Write color output to the console, only if {@link #isVerbose()} <code> == true</code>.
    */
   void printlnVerbose(ShellColor color, String output);

   /**
    * Clear the console.
    */
   void clear();

   /**
    * Execute a shell command.
    * 
    * @throws Throwable if an exception is encountered an {@link #isExceptionHandlingEnabled()} is not enabled.
    */
   void execute(String command) throws Exception;

   /**
    * Execute a shell script from the specified file.
    * 
    * @see {@link #execute(String)}
    */
   void execute(File file) throws IOException, Exception;

   /**
    * Execute a shell script from the specified file, passing the given arguments as input.
    * 
    * @see {@link #execute(File)}
    */
   void execute(File file, String... args) throws IOException, Exception;

   /**
    * Return true if the {@link Shell} is currently executing a plugin; otherwise, return false.
    */
   boolean isExecuting();

   /**
    * Wait for input. Return as soon as any key is pressed and return the scancode.
    */
   int scan();

   /**
    * Clear the current line of any text.
    */
   void clearLine();

   /**
    * Move the cursor x the specified number of positions.
    */
   void cursorLeft(int x);

   /**
    * Reset the shell prompt to default.
    */
   void setDefaultPrompt();

   /**
    * Set the current shell prompt, followed by '> '.
    */
   void setPrompt(String string);

   /**
    * Return the current shell prompt;
    */
   String getPrompt();

   /**
    * Set the stream from which the shell should read input.
    */
   void setInputStream(InputStream inputStream) throws IOException;

   /**
    * Set the stream to which the shell should print output.
    */
   void setOutputStream(OutputStream stream) throws IOException;

   /**
    * Return the current height, in characters, of the current shell console. (<strong>Warning:</strong> This may change
    * in the time between when the method is called and when the result is used. Be sure to call the method as close to
    * its actual use as possible.)
    */
   int getHeight();

   /**
    * Return the absolute height of the console. This may be different than getHeight() depending on how many lines are
    * available in a display buffer.
    * 
    * @return
    */
   int getAbsoluteHeight();

   /**
    * Return the current width, in characters, of the current shell console. (<strong>Warning:</strong> This may change
    * in the time between when the method is called and when the result is used. Be sure to call the method as close to
    * its actual use as possible.)
    */
   int getWidth();

   /**
    * Ask the current {@link InputStream} for data.
    * 
    * @return any read data as a string, or null if none available.
    * @throws IOException on error
    */
   String readLine() throws IOException;

   /**
    * Ask the current {@link InputStream} for input, masking keystrokes in the console with the given mask.
    * 
    * @param mask The character to use for masking input
    * @return any read data as a string, or null if none available.
    * @throws IOException on error
    */
   String readLine(Character mask) throws IOException;

   /**
    * Controls the shell's usage of ANSI escape code support. This method does not guarantee ANSI will function
    * properly, as the underlying Terminal must also support it.
    */
   void setAnsiSupported(boolean value);

   /**
    * Returns whether or not this shell supports ANSI escape codes.
    */
   boolean isAnsiSupported();

   /**
    * Register the buffer manager for the shell system
    * 
    * @param manager
    */
   void registerBufferManager(BufferManager manager);

   /**
    * Get buffer manager based on typed
    * 
    * @return
    */
   BufferManager getBufferManager();

   /**
    * Register a {@link KeyListener} object that will defined behavior when a given key is received from the
    * {@link Shell}
    */
   void registerKeyListener(KeyListener keyListener);

   /**
    * Place the shell output into buffering mode. Do not automatically render changes to the screen unless changed back
    * to {@link #directWriteMode()} or by calling {@link #flush()}
    */
   void bufferingMode();

   /**
    * Place the shell output in direct-write mode. All data printed to buffer will be immediately rendered.
    */
   void directWriteMode();

   /**
    * Get the current Forge environment.
    */
   ForgeEnvironment getEnvironment();

}

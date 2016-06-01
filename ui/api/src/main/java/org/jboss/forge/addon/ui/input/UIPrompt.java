/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

/**
 * Used when an addon needs to prompt for ad-hoc user input
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface UIPrompt
{
   /**
    * Prompt for user input, first printing the given line, then return user input as a String.
    */
   String prompt(String message);

   /**
    * First print the given message, prompt the user for input (masking keystrokes for secrecy) then return user input.
    */
   String promptSecret(String message);

   /**
    * Prompt for boolean user input (Y/n), first printing the given message, then returning user input as a boolean. The
    * value returned will default to <code>true</code> if an empty or whitespace-only user input is read.
    */
   boolean promptBoolean(String message);

   /**
    * Prompt for boolean user input, first printing the given message, then returning user input as a boolean. The value
    * returned will default to <code>defaultValue</code> if an empty or whitespace-only user input is read.
    */
   boolean promptBoolean(String message, boolean defaultValue);

}

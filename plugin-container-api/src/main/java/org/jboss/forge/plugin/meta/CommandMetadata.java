/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.meta;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Defines a command.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface CommandMetadata
{
   /**
    * Get the help text for this command.
    */
   String getHelp();

   /**
    * Get the {@link Method} that implements this command.
    */
   Method getMethod();

   /**
    * Get the name of this command.
    */
   String getName();

   /**
    * Get the option defined by the given name.
    * 
    * @throws IllegalArgumentException if no such option exists.
    */
   OptionMetadata getNamedOption(String name) throws IllegalArgumentException;

   /**
    * Return the number of ordered (unnamed) options defined by this command.
    */
   int getNumOrderedOptions();

   /**
    * Return the option at the given index in the plugin method signature, not the index of the option on the command
    * line.
    */
   OptionMetadata getOptionByAbsoluteIndex(int index);

   /**
    * Return a list of all options defined by this command.
    */
   List<OptionMetadata> getOptions();

   /**
    * Return the option at the given index as required on the command line, not the index of the option in the
    * implementing method signature.
    */
   OptionMetadata getOrderedOptionByIndex(int index) throws IllegalArgumentException;

   /**
    * Return the {@link PluginMetadata} containing this command.
    */
   PluginMetadata getParent();

   /**
    * Return true if this command has an {@link OptionMetadata} with the given name.
    */
   boolean hasOption(String name);

   /**
    * Return true if this command has any options.
    */
   boolean hasOptions();

   /**
    * Return true if this command accepts ordered options.
    */
   boolean hasOrderedOptions();

   /**
    * Return true if this command accepts options that declare a short name.
    */
   boolean hasShortOption(String name);

   /**
    * Return true if this command is the default command for its declaring {@link PluginMetadata}
    */
   boolean isDefault();

   /**
    * Return true if this command is the "setup" command.
    */
   boolean isSetup();

}

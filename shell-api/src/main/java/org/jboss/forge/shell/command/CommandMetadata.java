/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.shell.command;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.jboss.forge.resources.Resource;

/**
 * Defines a command.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface CommandMetadata
{
   /**
    * Get the help text for this comand.
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
    * Return the set of {@link Resource} types for which this command is in scope, or available.
    */
   @SuppressWarnings("rawtypes")
   Set<Class<? extends Resource>> getResourceScopes();

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
    * Return true if this command is usable with the given resource scope.
    */
   @SuppressWarnings("rawtypes")
   boolean usableWithResource(Class<? extends Resource> class1);

}

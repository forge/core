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

import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.completer.CommandCompleter;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;

/**
 * Defines an option.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface OptionMetadata
{
   /**
    * Return the boxed type for this option. E.g: If the literal type is 'int', the boxed type is {@link Integer}
    */
   Class<?> getBoxedType();

   /**
    * Return the type of the {@link CommandCompleter} for this option.
    */
   Class<? extends CommandCompleter> getCompleterType();

   /**
    * Get the default value for this option. Default values are used when no value is passed from the command line.
    */
   String getDefaultValue();

   /**
    * Get the description text for this option.
    */
   String getDescription();

   /**
    * Get the help text for this option.
    */
   String getHelp();

   /**
    * Get the index of this option in the receiving method signature parameter list.
    */
   int getIndex();

   /**
    * Get the name of this option.
    */
   String getName();

   /**
    * Get the verbose description of this option.
    */
   String getOptionDescriptor();

   /**
    * Get the {@link CommandMetadata} defining this option.
    */
   CommandMetadata getParent();

   /**
    * Get the requested {@link PromptType} for this option.
    */
   PromptType getPromptType();

   /**
    * Get the short name for this option, if it exists; otherwise, return empty string.
    */
   String getShortName();

   /**
    * Get the literal type of this option.
    */
   Class<?> getType();

   /**
    * Return true if this option specifies a custom {@link CommandCompleter}.
    */
   boolean hasCustomCompleter();

   /**
    * Return true if this option has a default value.
    */
   boolean hasDefaultValue();

   /**
    * Return true if this option is a boolean type.
    */
   boolean isBoolean();

   /**
    * Return true if this option is an enum type.
    */
   boolean isEnum();

   /**
    * Return true if this option is only a flag, and only has a short name, taking no value on the command line.
    */
   boolean isFlagOnly();

   /**
    * Return true if this option is named.
    */
   boolean isNamed();

   /**
    * Return true if this option is not named, and must be specified by order.
    */
   boolean isOrdered();

   /**
    * Return true if this option is the {@link PipeIn}
    */
   boolean isPipeIn();

   /**
    * Return true if this option is the {@link PipeOut}
    */
   boolean isPipeOut();

   /**
    * Return true if this option is required, and cannot be omitted on the command line.
    */
   boolean isRequired();

   /**
    * Return true if this option accepts multiple values on the command line.
    */
   boolean isVarargs();

   /**
    * Return true if this option is not ordered: E.g. It may be named, or may be a {@link PipeIn} or {@link PipeOut}
    */
   boolean notOrdered();

}

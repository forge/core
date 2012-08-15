/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.plugin.meta;


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
    * Get the short name for this option, if it exists; otherwise, return empty string.
    */
   String getShortName();

   /**
    * Get the literal type of this option.
    */
   Class<?> getType();

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

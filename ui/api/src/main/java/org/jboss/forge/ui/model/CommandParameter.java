/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.model;

/**
 * A {@link CommandParameter} object represents a possible input from the user.
 *
 * Implementations must also override {@link Object#hashCode()} and {@link Object#equals(Object)}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface CommandParameter
{
   /**
    * This parameter name.
    */
   public abstract String getName();

   public abstract String getShortName();

   /**
    * A human-readable description of this parameter
    * 
    */
   public abstract String getDescription();

   public abstract String getHelp();

   // TODO: Create a Validation object ? Maybe with the PromptType
   public abstract boolean isRequired();

   // public abstract Class<T> getType();

   public abstract String getDefaultValue();
}
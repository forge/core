/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.command;

import org.jboss.forge.container.util.Types;
import org.jboss.forge.plugin.PipeIn;
import org.jboss.forge.plugin.PipeOut;
import org.jboss.forge.plugin.meta.CommandMetadata;
import org.jboss.forge.plugin.meta.OptionMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class OptionMetadataImpl implements OptionMetadata
{
   private CommandMetadata parent;
   private Class<?> type;
   private int index;

   private String name = "";
   private String shortName = "";
   private String defaultValue = "";
   private String description = "";
   private String help = "";

   private boolean flagOnly = false;
   private boolean required = false;

   private boolean pipeOut;
   private boolean pipeIn;

   public OptionMetadataImpl()
   {
   }

   /**
    * Get an informational string describing this Option
    */
   @Override
   public String getOptionDescriptor()
   {
      StringBuilder appender = new StringBuilder("[");
      if (isNamed())
      {
         appender.append(name).append("=");
      }

      if (getDescription().equals(""))
      {
         appender.append("ARG");
      }
      else
      {
         appender.append(description);
      }

      appender.append(" (of type ").append(getTypeDescriptor(type)).append(")");

      return appender.append(']').toString();
   }

   public static String getTypeDescriptor(Class<?> type)
   {
      if (Number.class.isAssignableFrom(Types.boxPrimitive(type)))
      {
         return "numeric";
      }
      else
      {
         return type.getCanonicalName();
      }
   }
   
   /**
    * Return whether this option is to be mapped via name or via parameter order.
    */
   @Override
   public boolean isNamed()
   {
      return (name != null) && !"".equals(name);
   }

   /**
    * Return the Boxed type of this Option, e.g: If the option is actual an <code>int.class</code>, return
    * <code>Integer.class</code> instead.
    */
   @Override
   public Class<?> getBoxedType()
   {
      return Types.boxPrimitive(getType());
   }

   /**
    * Return the literal type represented by this Option, e.g: the actual method parameter type.
    */
   @Override
   public Class<?> getType()
   {
      return type;
   }

   public void setType(final Class<?> type)
   {
      this.type = type;
   }

   /**
    * Return the name of this Option, if it has one
    */
   @Override
   public String getName()
   {
      return name;
   }

   public void setName(final String name)
   {
      this.name = name;
   }

   /**
    * Get the short name of this option, if it has one.
    */
   @Override
   public String getShortName()
   {
      return shortName;
   }

   public void setShortName(final String shortName)
   {
      this.shortName = shortName;
   }

   /**
    * Return whether or not this option is purely a boolean flag.
    */
   @Override
   public boolean isFlagOnly()
   {
      return flagOnly;
   }

   public void setFlagOnly(final boolean flagOnly)
   {
      this.flagOnly = flagOnly;
   }

   /**
    * Return the description of this Option
    */
   @Override
   public String getDescription()
   {
      return description;
   }

   public void setDescription(final String description)
   {
      this.description = description;
   }

   @Override
   public int getIndex()
   {
      return index;
   }

   public void setIndex(final int index)
   {
      this.index = index;
   }

   /**
    * Get the help text for this Option
    */
   @Override
   public String getHelp()
   {
      return help;
   }

   public void setHelp(final String help)
   {
      this.help = help;
   }

   /**
    * Return whether or not this option requires a value at execution time.
    */
   @Override
   public boolean isRequired()
   {
      return required;
   }

   public void setRequired(final boolean required)
   {
      this.required = required;
   }

   @Override
   public String toString()
   {
      return name + ":" + description;
   }

   /**
    * Get the parent Command of this Option
    */
   @Override
   public CommandMetadata getParent()
   {
      return parent;
   }

   public void setParent(final CommandMetadata parent)
   {
      this.parent = parent;
   }

   /**
    * Return whether or not this option represents a {@link Boolean} type
    */
   @Override
   public boolean isBoolean()
   {
      return (Boolean.TYPE.equals(getType()) || Boolean.class.equals(getType()));
   }

   /**
    * Return whether or not this option represents an {@link Enum} type.
    */
   @Override
   public boolean isEnum()
   {
      return (getType() != null) && getType().isEnum();
   }

   /**
    * Return whether or not this option represents a Varargs parameter type
    */
   @Override
   public boolean isVarargs()
   {
      return getType().isArray();
   }

   /**
    * Return the default value for this Option, if specified
    */
   @Override
   public String getDefaultValue()
   {
      return defaultValue;
   }

   public void setDefaultValue(final String defaultValue)
   {
      this.defaultValue = defaultValue;
   }

   /**
    * Return whether or not this Option provides a default value
    */
   @Override
   public boolean hasDefaultValue()
   {
      return (defaultValue != null) && !"".equals(defaultValue);
   }

   /**
    * Return whether or not this Option is a {@link PipeOut}
    */
   @Override
   public boolean isPipeOut()
   {
      return pipeOut;
   }

   public void setPipeOut(final boolean pipeOut)
   {
      this.pipeOut = pipeOut;
   }

   /**
    * Return whether or not this Option is a {@link PipeIn}
    */
   @Override
   public boolean isPipeIn()
   {
      return pipeIn;
   }

   public void setPipeIn(final boolean pipeIn)
   {
      this.pipeIn = pipeIn;
   }

   /**
    * Return whether or not this Option is not ordered, e.g: It might have a name, or be an input/output pipe.
    */
   @Override
   public boolean notOrdered()
   {
      return pipeIn || pipeOut || isNamed();
   }

   @Override
   public boolean isOrdered()
   {
      return !notOrdered();
   }

}

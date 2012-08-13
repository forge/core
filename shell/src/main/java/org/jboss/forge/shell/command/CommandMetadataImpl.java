/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.forge.resources.Resource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CommandMetadataImpl implements CommandMetadata
{
   private PluginMetadata parent;
   private Method method;

   private boolean isDefault = false;
   private boolean isSetup = false;

   private String name = "";
   private String help = "";
   private List<OptionMetadata> options = new ArrayList<OptionMetadata>();

   @SuppressWarnings("rawtypes")
   private Set<Class<? extends Resource>> resourceScopes = Collections.emptySet();

   @Override
   public OptionMetadata getNamedOption(final String name) throws IllegalArgumentException
   {
      for (OptionMetadata option : options)
      {
         if (option.isNamed() && (option.getName().equals(name) || option.getShortName().equals(name)))
         {
            return option;
         }
      }
      throw new IllegalArgumentException("No such option [" + name + "] for command: " + this);
   }

   @Override
   public OptionMetadata getOptionByAbsoluteIndex(final int index)
   {
      for (OptionMetadata option : options)
      {
         if (option.getIndex() == index)
         {
            return option;
         }
      }
      throw new IllegalArgumentException("No option with index [" + index + "] exists for command: " + this);

   }

   @Override
   public OptionMetadata getOrderedOptionByIndex(final int index) throws IllegalArgumentException
   {
      int currentIndex = 0;
      for (OptionMetadata option : options)
      {
         if (option.isOrdered() && (index == currentIndex))
         {
            return option;
         }
         else if (option.isOrdered())
         {
            currentIndex++;
         }
      }
      throw new IllegalArgumentException("No option with index [" + index + "] exists for command: " + this);
   }

   @Override
   public int getNumOrderedOptions()
   {
      int count = 0;
      for (OptionMetadata option : options)
      {
         if (option.isOrdered())
         {
            count++;
         }
      }
      return count;
   }

   @Override
   public Method getMethod()
   {
      return method;
   }

   public void setMethod(final Method method)
   {
      this.method = method;
   }

   @Override
   public boolean isDefault()
   {
      return isDefault;
   }

   public void setDefault(final boolean isDefault)
   {
      this.isDefault = isDefault;
   }

   @Override
   public boolean isSetup()
   {
      return isSetup;
   }

   public void setSetup(final boolean isSetup)
   {
      this.isSetup = isSetup;
   }

   @Override
   public String getName()
   {
      return name;
   }

   public void setName(final String name)
   {
      this.name = name;
   }

   @Override
   public List<OptionMetadata> getOptions()
   {
      if (options == null)
      {
         options = new ArrayList<OptionMetadata>();
      }
      Collections.sort(options, new Comparator<OptionMetadata>()
      {
         @Override
         public int compare(final OptionMetadata l, final OptionMetadata r)
         {
            if (l == r)
            {
               return 0;
            }
            if ((l != r) && (l == null))
            {
               return 1;
            }
            if ((l != r) && (r == null))
            {
               return -1;
            }

            if (l.getIndex() == r.getIndex())
            {
               return 0;
            }
            if (l.getIndex() > r.getIndex())
            {
               return 1;
            }
            if (l.getIndex() < r.getIndex())
            {
               return -1;
            }

            return 0;
         }
      });
      return options;
   }

   public void addOption(final OptionMetadata option)
   {
      this.options.add(option);
   }

   @Override
   public String getHelp()
   {
      return help;
   }

   public void setHelp(final String help)
   {
      this.help = help;
   }

   @Override
   public String toString()
   {
      return name;
   }

   @Override
   public PluginMetadata getParent()
   {
      return parent;
   }

   public void setParent(final PluginMetadata parent)
   {
      this.parent = parent;
   }

   @Override
   public boolean hasOptions()
   {
      return !getOptions().isEmpty();
   }

   @Override
   public boolean hasShortOption(final String name)
   {
      for (OptionMetadata option : options)
      {
         if (option.isNamed() && option.getShortName().equals(name))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean hasOption(final String name)
   {
      for (OptionMetadata option : options)
      {
         if (option.isNamed() &&
                  (option.getName().equals(name)
                  || ((option.getShortName().equals(name))
                  && !option.getShortName().isEmpty())))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Set<Class<? extends Resource>> getResourceScopes()
   {
      return resourceScopes;
   }

   @SuppressWarnings("rawtypes")
   public void setResourceScopes(final List<Class<? extends Resource>> resourceScopes)
   {
      this.resourceScopes = new HashSet<Class<? extends Resource>>(resourceScopes);
   }

   @SuppressWarnings("rawtypes")
   public boolean usableWithResource(final Class<? extends Resource> resource)
   {
      return (this.resourceScopes.size() == 0) || this.resourceScopes.contains(resource);
   }

   @Override
   public boolean hasOrderedOptions()
   {
      try
      {
         getOrderedOptionByIndex(0);
         return true;
      }
      catch (Exception e)
      {
         return false;
      }
   }
}
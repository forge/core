/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.parser.java.util.Strings;

public class PluginEntry
{
   private final String name;
   private final String apiVersion;
   private final String slot;

   public PluginEntry(final String name, final String apiVersion, final String slot)
   {
      this.name = name;
      this.apiVersion = apiVersion;
      this.slot = slot;
   }

   public PluginEntry(final String name, final String apiVersion)
   {
      this.name = name;
      this.apiVersion = apiVersion;
      this.slot = null;
   }

   public PluginEntry(final String name)
   {
      this.name = name;
      this.apiVersion = null;
      this.slot = null;
   }

   public String getName()
   {
      return name;
   }

   public String getApiVersion()
   {
      return apiVersion;
   }

   public String getSlot()
   {
      return slot;
   }

   @Override
   public String toString()
   {
      return name + ":" + apiVersion + ":" + slot;
   }

   public static PluginEntry fromCoordinates(final String coordinates)
   {
      String[] split = coordinates.split(":");
      List<String> tokens = Arrays.asList(split);

      if (tokens.size() == 3)
      {
         if (Strings.isNullOrEmpty(tokens.get(0)))
            throw new IllegalArgumentException("Name was empty [" + coordinates + "]");
         if (Strings.isNullOrEmpty(tokens.get(1)))
            throw new IllegalArgumentException("Version was empty [" + coordinates + "]");
         if (Strings.isNullOrEmpty(tokens.get(2)))
            throw new IllegalArgumentException("Slot was empty [" + coordinates + "]");

         return new PluginEntry(tokens.get(0), tokens.get(1), tokens.get(2));
      }
      else
      {
         throw new IllegalArgumentException("Coordinates must be of the form 'name:apiVersion:slot'");
      }

   }

   public String toModuleId()
   {
      return name + ":" + slot;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((apiVersion == null) ? 0 : apiVersion.hashCode());
      result = (prime * result) + ((name == null) ? 0 : name.hashCode());
      result = (prime * result) + ((slot == null) ? 0 : slot.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PluginEntry other = (PluginEntry) obj;
      if (apiVersion == null)
      {
         if (other.apiVersion != null)
            return false;
      }
      else if (!apiVersion.equals(other.apiVersion))
         return false;
      if (name == null)
      {
         if (other.name != null)
            return false;
      }
      else if (!name.equals(other.name))
         return false;
      if (slot == null)
      {
         if (other.slot != null)
            return false;
      }
      else if (!slot.equals(other.slot))
         return false;
      return true;
   }

   public String toCoordinates()
   {
      return toString();
   }

}
package org.jboss.forge.container;

import java.util.Arrays;
import java.util.List;

public class AddonEntry
{
   private static final String DEFAULT_SLOT = "main";
   private final String name;
   private final String apiVersion;
   private final String slot;

   protected AddonEntry(final String name, final String apiVersion, final String slot)
   {
      this.name = name;
      this.apiVersion = apiVersion;
      this.slot = slot;
   }

   protected AddonEntry(final String name, final String apiVersion)
   {
      this.name = name;
      this.apiVersion = apiVersion;
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
      return slot == null ? DEFAULT_SLOT : slot;
   }

   @Override
   public String toString()
   {
      return toCoordinates();
   }

   public static AddonEntry fromCoordinates(final String coordinates)
   {
      String[] split = coordinates.split(":");
      List<String> tokens = Arrays.asList(split);

      if (tokens.size() == 3)
      {
         if (tokens.get(0) == null || tokens.get(0).isEmpty())
            throw new IllegalArgumentException("Name was empty [" + coordinates + "]");
         if (tokens.get(1) == null || tokens.get(1).isEmpty())
            throw new IllegalArgumentException("Version was empty [" + coordinates + "]");
         if (tokens.get(2) == null || tokens.get(2).isEmpty())
            throw new IllegalArgumentException("Slot was empty [" + coordinates + "]");

         return new AddonEntry(tokens.get(0), tokens.get(1), tokens.get(2));
      }
      else
      {
         throw new IllegalArgumentException("Coordinates must be of the form 'name:apiVersion:slot'");
      }

   }

   public static AddonEntry from(String name, String apiVersion)
   {
      return new AddonEntry(name, apiVersion);
   }

   public static AddonEntry from(String name, String apiVersion, String slot)
   {
      return new AddonEntry(name, apiVersion, slot);
   }

   public String toCoordinates()
   {
      return getName() + ":" + getApiVersion() + ":" + getSlot();
   }

   public String toModuleId()
   {
      return getName() + ":" + getSlot();
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
      AddonEntry other = (AddonEntry) obj;
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
}
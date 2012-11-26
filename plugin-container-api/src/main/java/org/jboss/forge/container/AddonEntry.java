package org.jboss.forge.container;

import java.util.Arrays;
import java.util.List;

public class AddonEntry
{
   private static final String NONE = null;
   private final String name;
   private final String apiVersion;
   private final String version;

   protected AddonEntry(final String name, final String version, final String apiVersion)
   {
      this.name = name;
      this.version = version;
      this.apiVersion = apiVersion;
   }

   public AddonEntry(String name, String version)
   {
      this.name = name;
      this.version = version;
      this.apiVersion = NONE;
   }

   public String getName()
   {
      return name;
   }

   public String getApiVersion()
   {
      return apiVersion == null ? "" : apiVersion;
   }

   public String getVersion()
   {
      return version;
   }

   @Override
   public String toString()
   {
      return toCoordinates();
   }

   public static AddonEntry fromCoordinates(final String coordinates)
   {
      String[] split = coordinates.split(",");
      List<String> tokens = Arrays.asList(split);

      if (tokens.size() >= 2)
      {
         if (tokens.get(0) == null || tokens.get(0).isEmpty())
            throw new IllegalArgumentException("Name was empty [" + coordinates + "]");
         if (tokens.get(1) == null || tokens.get(1).isEmpty())
            throw new IllegalArgumentException("Version was empty [" + coordinates + "]");
      }
      else
      {
         throw new IllegalArgumentException(
                  "Coordinates must be of the form 'name,version' or 'name,version,api-version");
      }

      if (tokens.size() == 3)
      {
         if (tokens.get(2) == null || tokens.get(2).isEmpty())
            throw new IllegalArgumentException("API version was empty [" + coordinates + "]");
         return new AddonEntry(tokens.get(0), tokens.get(1), tokens.get(2));
      }
      return new AddonEntry(tokens.get(0), tokens.get(1));

   }

   public static AddonEntry from(String name, String version)
   {
      return new AddonEntry(name, version);
   }

   public static AddonEntry from(String name, String version, String apiVersion)
   {
      return new AddonEntry(name, version, apiVersion);
   }

   public String toCoordinates()
   {
      return getName() + "," + getVersion() + "," + getApiVersion();
   }

   public String toModuleId()
   {
      return getName().replaceAll(":", ".") + ":" + getVersion();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((apiVersion == null) ? 0 : apiVersion.hashCode());
      result = (prime * result) + ((name == null) ? 0 : name.hashCode());
      result = (prime * result) + ((version == null) ? 0 : version.hashCode());
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
      if (version == null)
      {
         if (other.version != null)
            return false;
      }
      else if (!version.equals(other.version))
         return false;
      return true;
   }
}
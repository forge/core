package org.jboss.forge.furnace.addons;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;

public class AddonId implements Comparable<AddonId>
{
   private String name;
   private Version apiVersion;
   private Version version;

   public String getName()
   {
      return name;
   }

   public Version getApiVersion()
   {
      return apiVersion;
   }

   public Version getVersion()
   {
      return version;
   }

   @Override
   public String toString()
   {
      return toCoordinates();
   }

   public static AddonId fromCoordinates(final String coordinates)
   {
      String[] split = coordinates.split(",");
      List<String> tokens = Arrays.asList(split);

      if (tokens.size() < 2)
      {
         throw new IllegalArgumentException(
                  "Coordinates must be of the form 'name,version' or 'name,version,api-version");
      }

      if (tokens.size() == 3)
      {
         if (tokens.get(2) == null || tokens.get(2).isEmpty())
            throw new IllegalArgumentException("API version was empty [" + coordinates + "]");
         return from(tokens.get(0), tokens.get(1), tokens.get(2));
      }
      return from(tokens.get(0), tokens.get(1));

   }

   public static AddonId from(String name, String version)
   {
      return from(name, version, null);
   }

   public static AddonId from(String name, String version, String apiVersion)
   {
      Assert.notNull(name, "Name cannot be null.");
      if (name.trim().isEmpty())
         throw new IllegalArgumentException("Name cannot be empty.");
      Assert.notNull(version, "Version cannot be null.");
      if (version.trim().isEmpty())
         throw new IllegalArgumentException("Version cannot be empty.");

      AddonId id = new AddonId();

      id.name = name;
      id.version = new SingleVersion(version);
      if (apiVersion == null || apiVersion.trim().isEmpty())
         id.apiVersion = null;
      else
         id.apiVersion = new SingleVersion(apiVersion);

      return id;

   }

   /**
    * The name and version, comma separated.
    */
   public String toCoordinates()
   {
      StringBuilder coord = new StringBuilder(getName()).append(",").append(getVersion());
      return coord.toString();
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof AddonId))
         return false;
      AddonId other = (AddonId) obj;
      if (name == null)
      {
         if (other.getName() != null)
            return false;
      }
      else if (!name.equals(other.getName()))
         return false;
      if (version == null)
      {
         if (other.getVersion() != null)
            return false;
      }
      else if (!version.equals(other.getVersion()))
         return false;
      return true;
   }

   @Override
   public int compareTo(AddonId o)
   {
      if (o == null)
      {
         return -1;
      }
      return toCoordinates().compareTo(o.toCoordinates());
   }
}
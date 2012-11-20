package org.jboss.forge.container;

public class AddonDependency
{
   private String name;
   private String minVersion = null;
   private String maxVersion = null;

   private boolean optional = false;

   public AddonDependency(String name, String minVersion, String maxVersion)
   {
      this(name, minVersion, maxVersion, false);
   }

   public AddonDependency(String name, String minVersion, String maxVersion, boolean optional)
   {
      this.optional = optional;
      this.name = name;
      this.minVersion = minVersion;
      this.maxVersion = maxVersion;
   }

   public String getName()
   {
      return name;
   }

   public String getMinVersion()
   {
      return minVersion;
   }

   public String getMaxVersion()
   {
      return maxVersion;
   }

   public boolean isOptional()
   {
      return optional;
   }

   @Override
   public String toString()
   {
      return "AddonDependency [name=" + name + ", minVersion=" + minVersion + ", maxVersion=" + maxVersion
               + ", optional=" + optional + "]";
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AddonDependency other = (AddonDependency) obj;
      if (name == null)
      {
         if (other.name != null)
            return false;
      }
      else if (!name.equals(other.name))
         return false;
      return true;
   }

}
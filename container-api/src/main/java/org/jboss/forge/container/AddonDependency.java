package org.jboss.forge.container;

public class AddonDependency
{
   private AddonId addon;
   private boolean export = false;
   private boolean optional = false;

   public static AddonDependency create(AddonId addon)
   {
      return create(addon, false);
   }

   public static AddonDependency create(AddonId addon, boolean export)
   {
      return create(addon, export, false);
   }

   public static AddonDependency create(AddonId addon, boolean export, boolean optional)
   {
      return new AddonDependency(addon, export, optional);
   }

   private AddonDependency(AddonId addon, boolean export, boolean optional)
   {
      this.addon = addon;
      this.export = export;
      this.optional = optional;
   }

   public AddonId getId()
   {
      return addon;
   }

   public boolean isExport()
   {
      return export;
   }

   public boolean isOptional()
   {
      return optional;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((addon == null) ? 0 : addon.hashCode());
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
      if (addon == null)
      {
         if (other.addon != null)
            return false;
      }
      else if (!addon.equals(other.addon))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "(addon=" + addon + ", export=" + export + ", optional=" + optional + ")";
   }

}
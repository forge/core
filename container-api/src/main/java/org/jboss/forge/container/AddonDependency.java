package org.jboss.forge.container;

public class AddonDependency
{
   public enum ExportType
   {
      NONE, ONDEMAND, ALWAYS
   }

   private AddonEntry addon;
   private ExportType exportType = ExportType.NONE;
   private boolean optional = false;

   public static AddonDependency create(AddonEntry addon, ExportType export, boolean optional)
   {
      return new AddonDependency(addon, export, optional);
   }

   private AddonDependency(AddonEntry addon, ExportType export, boolean optional)
   {
      this.addon = addon;
      this.exportType = export;
      this.optional = optional;
   }

   public AddonEntry getAddon()
   {
      return addon;
   }

   public ExportType getExportType()
   {
      return exportType;
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

}
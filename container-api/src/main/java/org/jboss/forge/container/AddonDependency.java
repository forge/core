package org.jboss.forge.container;

public class AddonDependency
{
   public enum ExportType
   {
      NONE, ONDEMAND, ALWAYS
   }

   private String name;
   private String version = null;
   private ExportType exportType = ExportType.NONE;
   private boolean optional = false;

   public AddonDependency(String name, String version, ExportType exportType, boolean optional)
   {
      this.name = name;
      this.version = version;
      this.exportType = exportType;
      this.optional = optional;
   }

   public String getName()
   {
      return name;
   }

   public String getVersion()
   {
      return version;
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
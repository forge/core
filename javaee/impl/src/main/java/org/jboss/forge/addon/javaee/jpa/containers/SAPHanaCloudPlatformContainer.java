package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;

public class SAPHanaCloudPlatformContainer extends JavaEEDefaultContainer
{

   private static final String DEFAULT_DATA_SOURCE = "jdbc/DefaultDB";

   @Override
   public String getDefaultDataSource()
   {
      return DEFAULT_DATA_SOURCE;
   }

   @Override
   public DatabaseType getDefaultDatabaseType()
   {
      return DatabaseType.HSQLDB;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "SAP HANA Cloud Platform" : "SAP_HCP";
   }
}

package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;

public class SAPLeanJavaServerContainer extends JavaEEDefaultContainer
{

   private static final String DEFAULT_DATA_SOURCE = "jdbc/DefaultDB";

   @Override
   protected String getDefaultDataSource()
   {
      return DEFAULT_DATA_SOURCE;
   }

   @Override
   protected DatabaseType getDefaultDatabaseType()
   {
      return DatabaseType.HSQLDB;
   }

   @Override
   public String getName()
   {
      return "SAP Lean Java Server";
   }
}

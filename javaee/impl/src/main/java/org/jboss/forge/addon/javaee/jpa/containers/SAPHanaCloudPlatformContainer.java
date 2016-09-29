/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;

public class TomEEContainer extends JavaEEDefaultContainer
{
   private static final String DEFAULT_DATASOURCE_NAME = "Default JDBC Database";

   @Override
   public DatabaseType getDefaultDatabaseType()
   {
      return DatabaseType.HSQLDB;
   }

   @Override
   public String getDefaultDataSource()
   {
      return DEFAULT_DATASOURCE_NAME;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "TomEE" : "TOMEE";
   }
}

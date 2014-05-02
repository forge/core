/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;

public class TomEEContainer extends JavaEEDefaultContainer
{

   /**
    * When you do not specify a jta-data-source and non-jta-data-source,
    * TomEE will automatically look for a suitable DataSource from the
    * tomee.xml config.  The matching is quite intelligent and will start
    * with the persistence unit name, but also try the webapp name and
    * even the ear name if applicable.
    * 
    * If one is not found, defaults are created.
    */
   @Override
   public boolean isDataSourceRequired()
   {
      return false;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "TomEE" : "TOMEE";
   }
}

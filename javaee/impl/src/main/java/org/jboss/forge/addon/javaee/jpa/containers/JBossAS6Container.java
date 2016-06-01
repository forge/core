/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class JBossAS6Container extends JavaEE6Container
{
   private static final String DEFAULT_DS = "java:/DefaultDS";

   @Override
   public DatabaseType getDefaultDatabaseType()
   {
      return DatabaseType.HSQLDB;
   }

   @Override
   public String getDefaultDataSource()
   {
      return DEFAULT_DS;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "JBoss Application Server 6.x" : "JBOSS_AS6";
   }

}

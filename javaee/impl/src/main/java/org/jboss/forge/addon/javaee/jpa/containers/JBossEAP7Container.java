/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.jpa.DatabaseType;

/**
 * Support for JBoss EAP 7.x container
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class JBossEAP7Container extends JavaEE7Container
{
   private static final String EXAMPLE_DS = "java:jboss/datasources/ExampleDS";

   @Override
   public DatabaseType getDefaultDatabaseType()
   {
      return DatabaseType.H2;
   }

   @Override
   public String getDefaultDataSource()
   {
      return EXAMPLE_DS;
   }

   @Override
   public String getName(boolean isGUI)
   {
      return isGUI ? "JBoss Enterprise Application Platform 7.x" : "JBOSS_EAP7";
   }
}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.container;

import javax.inject.Inject;

import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.spec.javaee.jpa.api.DatabaseType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class JBossAS7Container extends JavaEEDefaultContainer
{
   private static final String EXAMPLE_DS = "java:jboss/datasources/ExampleDS";

   @Inject
   private ShellPrintWriter writer;

   @Override
   public ShellPrintWriter getWriter()
   {
      return writer;
   }

   @Override
   protected DatabaseType getDefaultDatabaseType()
   {
      return DatabaseType.HSQLDB;
   }

   @Override
   protected String getDefaultDataSource()
   {
      return EXAMPLE_DS;
   }
}

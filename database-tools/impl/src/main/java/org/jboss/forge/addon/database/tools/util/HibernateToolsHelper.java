/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.concurrent.Callable;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.util.ClassLoaders;

public class HibernateToolsHelper
{
   public static void buildMappings(URL[] urls, final String driverName, final JDBCMetaDataConfiguration result)
            throws Exception
   {
      ClassLoaders.executeIn(urls, new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            Driver driver = (Driver) Class.forName(driverName, true, Thread.currentThread().getContextClassLoader())
                     .newInstance();
            DelegatingDriver delegatingDriver = new DelegatingDriver(driver);
            try
            {
               DriverManager.registerDriver(delegatingDriver);
               result.readFromJDBC();
            }
            finally
            {
               DriverManager.deregisterDriver(delegatingDriver);
            }
            return null;
         }
      });
   }

   public static URL[] getDriverUrls(FileResource<?> resource)
   {
      try
      {
         return new URL[] { resource.getUnderlyingResourceObject().toURI().toURL() };
      }
      catch (MalformedURLException e)
      {
         return null;
      }
   }
}

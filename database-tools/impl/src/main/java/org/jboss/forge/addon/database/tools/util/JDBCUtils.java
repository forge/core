/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.database.tools.util;

import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.database.tools.generate.Database;
import org.jboss.forge.addon.database.tools.generate.GenerateEntitiesCommandDescriptor;
import org.jboss.forge.furnace.util.ClassLoaders;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public final class JDBCUtils
{
   private static final Logger logger = Logger.getLogger(JDBCUtils.class.getName());

   private JDBCUtils()
   {
   }

   public static Database getDatabaseInfo(GenerateEntitiesCommandDescriptor descriptor) throws Exception
   {
      URL[] urls = descriptor.getUrls();
      Properties p = descriptor.getConnectionProperties();
      String driverName = p.getProperty("hibernate.connection.driver_class");
      String url = p.getProperty("hibernate.connection.url");
      String userName = p.getProperty("hibernate.connection.username");
      String password = p.getProperty("hibernate.connection.password");
      return ClassLoaders.executeIn(urls, new Callable<Database>()
      {
         @Override
         public Database call() throws Exception
         {
            Database database;
            Driver driver = (Driver) Class.forName(driverName, true, Thread.currentThread().getContextClassLoader())
                     .newInstance();
            Properties p = new Properties();
            p.setProperty("user", userName);
            p.setProperty("password", password);
            try (Connection con = driver.connect(url, p))
            {
               // Some drivers (erroneously) return null
               if (con == null)
               {
                  throw new SQLException(String.format("Cannot connect to %s with driver %s", url, driverName));
               }
               String connectionSchema = null;
               try
               {
                  connectionSchema = con.getSchema();
               }
               catch (Throwable e)
               {
                  logger.log(Level.SEVERE, "Error while fetching schema from generator. Will be ignored", e);
               }
               String connectionCatalog = con.getCatalog();
               database = new Database(connectionCatalog, connectionSchema);
               DatabaseMetaData metaData = con.getMetaData();
               try (ResultSet rs = metaData.getTables(null, null, "%%", new String[] { "TABLE" }))
               {
                  while (rs.next())
                  {
                     String catalog = rs.getString("TABLE_CAT");
                     if (catalog == null)
                     {
                        catalog = connectionCatalog;
                     }
                     String schema = rs.getString("TABLE_SCHEM");
                     if (schema == null)
                     {
                        schema = connectionSchema;
                     }
                     String name = rs.getString("TABLE_NAME");
                     database.addTable(catalog, schema, name);
                  }
               }
            }
            return database;
         }
      });
   }

}

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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.jboss.forge.addon.database.tools.generate.DatabaseTable;
import org.jboss.forge.addon.database.tools.generate.GenerateEntitiesCommandDescriptor;
import org.jboss.forge.furnace.util.ClassLoaders;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class JDBCUtils
{

   private JDBCUtils()
   {
   }

   public static List<DatabaseTable> getTables(GenerateEntitiesCommandDescriptor descriptor) throws Exception
   {
      List<DatabaseTable> tables = new ArrayList<>();
      URL[] urls = descriptor.getUrls();
      Properties p = descriptor.getConnectionProperties();
      String driverName = p.getProperty("hibernate.connection.driver_class");
      String url = p.getProperty("hibernate.connection.url");
      String userName = p.getProperty("hibernate.connection.username");
      String password = p.getProperty("hibernate.connection.password");
      ClassLoaders.executeIn(urls, new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            Driver driver = (Driver) Class.forName(driverName, true, Thread.currentThread().getContextClassLoader())
                     .newInstance();
            Properties p = new Properties();
            p.setProperty("user", userName);
            p.setProperty("password", password);
            try (Connection con = driver.connect(url, p))
            {
               DatabaseMetaData metaData = con.getMetaData();
               try (ResultSet rs = metaData.getTables(null, null, "%%", null))
               {
                  while (rs.next())
                  {
                     String catalog = rs.getString("TABLE_CAT");
                     String schema = rs.getString("TABLE_SCHEM");
                     String name = rs.getString("TABLE_NAME");
                     DatabaseTable table = new DatabaseTable(catalog, schema, name);
                     tables.add(table);
                  }
               }
            }
            return null;
         }
      });
      return tables;
   }

}

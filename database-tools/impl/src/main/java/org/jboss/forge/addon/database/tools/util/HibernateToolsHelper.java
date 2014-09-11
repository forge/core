package org.jboss.forge.addon.database.tools.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.util.ClassLoaders;

public class HibernateToolsHelper
{
   public void buildMappings(URL[] urls, final String driverName, final JDBCMetaDataConfiguration result)
            throws Exception
   {
      ClassLoaders.executeIn(urls, new Callable<Void>()
      {
         @Override
         public Void call() throws Exception
         {
            Driver driver = (Driver) Class.forName(driverName, true, Thread.currentThread().getContextClassLoader())
                     .newInstance();
            DriverManager.registerDriver(new DelegatingDriver(driver));
            result.readFromJDBC();
            result.buildMappings();
            return null;
         }
      });
   }

   public URL[] getDriverUrls(FileResource<?> resource)
   {
      try
      {
         File file = (File) resource.getUnderlyingResourceObject();
         ArrayList<URL> result = new ArrayList<URL>(1);
         result.add(file.toURI().toURL());
         return result.toArray(new URL[1]);
      }
      catch (MalformedURLException e)
      {
         return null;
      }
   }
}

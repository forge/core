package org.jboss.forge.addon.database.tools.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.jboss.forge.addon.resource.FileResource;

public class HibernateToolsHelper
{
      
   public void buildMappings(
            URL[] urls, 
            final String driverName, 
            final JDBCMetaDataConfiguration result)
   {
      UrlClassLoaderExecutor.execute(urls, new Runnable() {
         @Override
         public void run()
         {
            try
            {
               Driver driver = (Driver) Class.forName(
                        driverName,
                        true,
                        Thread.currentThread().getContextClassLoader()).newInstance();
               DriverManager.registerDriver(new DelegatingDriver(driver));
               result.readFromJDBC();
               result.buildMappings();
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
     });
   }

   public URL[] getDriverUrls(FileResource<?> resource)
   {
      try {
         File file = (File)resource.getUnderlyingResourceObject();
         ArrayList<URL> result = new ArrayList<URL>(1);
         result.add(file.toURI().toURL());  
         return result.toArray(new URL[1]);
      } catch (MalformedURLException e) {
         return null;
      }
   }
}

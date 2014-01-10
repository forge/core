package org.jboss.forge.addon.database.tools.connections;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.UIValidator;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;

public class ConnectionProfileDetailsPage
{

   @Inject
   @WithAttributes(
            label = "JDBC URL",
            description = "The jdbc url for the database tables",
            required = true)
   protected UIInput<String> jdbcUrl;

   @Inject
   @WithAttributes(
            label = "User Name",
            description = "The user name for the database connection",
            required = true)
   protected UIInput<String> userName;

   @Inject
   @WithAttributes(
            label = "User Password",
            description = "The password for the database connection",
            required = false,
            defaultValue = "")
   protected UIInput<String> userPassword;

   @Inject
   @WithAttributes(
            label = "Hibernate Dialect",
            description = "The Hibernate dialect to use",
            required = true)
   protected UISelectOne<HibernateDialect> hibernateDialect;

   @Inject
   @WithAttributes(
            label = "Driver Location",
            description = "The location of the jar file that contains the JDBC driver",
            required = true)
   protected UIInput<FileResource<?>> driverLocation;

   @Inject
   @WithAttributes(
            label = "Driver Class",
            description = "The class name of the JDBC driver",
            required = true)
   protected UISelectOne<String> driverClass;
   
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder
               .add(jdbcUrl)
               .add(userName)
               .add(userPassword)
               .add(hibernateDialect)
               .add(driverLocation)
               .add(driverClass);
      hibernateDialect.setItemLabelConverter(new Converter<HibernateDialect, String>()
      {
         @Override
         public String convert(HibernateDialect dialect)
         {
            return dialect == null ? null : dialect.getDatabaseName() + " : " + dialect.getClassName();
         }
      });
      driverLocation.addValidator(new UIValidator()
      { 
         @Override
         public void validate(UIValidationContext context)
         {
            FileResource<?> resource = driverLocation.getValue();
            if (resource != null && !resource.exists()) {
               context.addValidationError(driverLocation, "The location '" + resource.getFullyQualifiedName() + "' does not exist");
            }
         }
      });
      driverClass.setValueChoices(new Callable<Iterable<String>>() {
         @Override
         public Iterable<String> call() throws Exception
         {
            return getDriverClassNames();
         }         
      });
      driverClass.setDefaultValue(new Callable<String>() {
         @Override
         public String call() throws Exception
         {
            String result = null;
            Iterator<String> iterator = driverClass.getValueChoices().iterator();
            if (iterator.hasNext()) {
               result = iterator.next();
            }
            return result;
         }         
      });

   }
   
   private List<String> getDriverClassNames() {
      ArrayList<String> result = new ArrayList<String>();
      FileResource<?> resource = driverLocation.getValue();
      if (resource != null && resource.exists()) {
    	 JarFile jarFile = null;
         try {
            File file = (File)resource.getUnderlyingResourceObject();
            URL[] urls = new URL[] { file.toURI().toURL() };
            URLClassLoader classLoader = URLClassLoader.newInstance(urls);
            Class<?> driverClass = classLoader.loadClass(Driver.class.getName());
            jarFile = new JarFile(file);
            Enumeration<JarEntry> iter = jarFile.entries();
            while (iter.hasMoreElements()) {
               JarEntry entry = iter.nextElement();
               if (entry.getName().endsWith(".class")) { 
                  String name = entry.getName();
                  name = name.substring(0, name.length() - 6);
                  name = name.replace('/', '.');
                  try {
                     Class<?> clazz = classLoader.loadClass(name);
                     if (driverClass.isAssignableFrom(clazz)) {
                        result.add(clazz.getName());
                     }
                  } catch (ClassNotFoundException cnfe) {
                     //ignore
                  } catch (NoClassDefFoundError err) {
                     //ignore
                  }
               }
            }
         } catch (Exception e) {
            // ignore and return an empty list
         } finally {
        	 if (jarFile != null) {
        		 try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	 }
         }
      }
      return result;
   }
   
   public void validate(UIValidationContext context)
   {
   }
   
}

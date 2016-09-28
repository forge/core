/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.database.tools.jpa.HibernateDialect;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.input.events.ValueChangeEvent;
import org.jboss.forge.addon.ui.validate.UIValidator;

@SuppressWarnings("rawtypes")
public abstract class AbstractConnectionProfileDetailsPage implements UICommand
{
   private static final Logger log = Logger.getLogger(AbstractConnectionProfileDetailsPage.class.getName());

   protected UIInput<String> jdbcUrl;
   protected UIInput<String> userName;
   protected UIInput<String> userPassword;
   protected UIInput<Boolean> saveUserPassword;
   protected UISelectOne<HibernateDialect> hibernateDialect;
   protected UIInput<FileResource> driverLocation;
   protected UISelectOne<Class> driverClass;
   private UIInput<Boolean> verifyConnection;

   private boolean connectionStale;
   private List<Class> driverClasses;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      jdbcUrl = factory.createInput("jdbcUrl", String.class).setLabel("JDBC URL")
               .setDescription("The jdbc url for the database tables").setRequired(true);
      userName = factory.createInput("userName", String.class).setLabel("User Name")
               .setDescription("The user name for the database connection").setRequired(true);
      userPassword = factory.createInput("userPassword", String.class).setLabel("User Password")
               .setDescription("The password for the database connection").setDefaultValue("");
      userPassword.getFacet(HintsFacet.class).setInputType(InputType.SECRET);
      saveUserPassword = factory.createInput("saveUserPassword", Boolean.class).setLabel("Save User Password?")
               .setDescription("Should the connection password be saved?");
      hibernateDialect = factory.createSelectOne("hibernateDialect", HibernateDialect.class)
               .setLabel("Hibernate Dialect")
               .setDescription("The Hibernate dialect to use").setRequired(true);
      driverLocation = factory.createInput("driverLocation", FileResource.class)
               .setLabel("Driver Location")
               .setDescription("The location of the jar file that contains the JDBC driver").setRequired(true);
      driverClass = factory.createSelectOne("driverClass", Class.class)
               .setLabel("Driver Class")
               .setDescription("The class name of the JDBC driver").setRequired(true);
      verifyConnection = factory.createInput("verifyConnection", Boolean.class).setLabel("Verify Database Connection")
               .setDescription("Attempt to connect to the database and verify connectivity");

      jdbcUrl.addValueChangeListener(new ConnectionStaleValueChangeListener());
      userName.addValueChangeListener(new ConnectionStaleValueChangeListener());
      userPassword.addValueChangeListener(new ConnectionStaleValueChangeListener());
      final boolean gui = builder.getUIContext().getProvider().isGUI();
      hibernateDialect.setItemLabelConverter(new Converter<HibernateDialect, String>()
      {
         @Override
         public String convert(HibernateDialect dialect)
         {
            if (dialect == null)
               return null;
            return (gui) ? dialect.getDatabaseName() + " : " + dialect.getClassName() : dialect.getClassName();
         }
      }).addValueChangeListener(new ConnectionStaleValueChangeListener());

      driverLocation.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext context)
         {
            FileResource<?> resource = driverLocation.getValue();
            if (resource != null && !resource.exists())
            {
               context.addValidationError(driverLocation, "The location '" + resource.getFullyQualifiedName()
                        + "' does not exist");
            }
         }
      });
      driverLocation.addValueChangeListener(new ConnectionStaleValueChangeListener());
      driverLocation.addValueChangeListener(new DriverNamesStaleValueChangeListener());

      driverClass.setValueChoices(new LocateDriverClassNamesCallable())
               .setItemLabelConverter(Class::getName)
               .setDefaultValue(new Callable<Class>()
               {
                  @Override
                  public Class call() throws Exception
                  {
                     Class result = null;
                     Iterator<Class> iterator = driverClass.getValueChoices().iterator();
                     if (iterator.hasNext())
                     {
                        result = iterator.next();
                     }
                     return result;
                  }
               }).addValueChangeListener(new ConnectionStaleValueChangeListener());

      verifyConnection.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext context)
         {
            Boolean value = (Boolean) context.getCurrentInputComponent().getValue();
            if (value != null && value && connectionStale)
            {
               Properties properties = new Properties();
               properties.setProperty("user", userName.getValue());
               if (userPassword.hasValue())
                  properties.setProperty("password", userPassword.getValue());
               try
               {
                  Driver driver = (Driver) driverClass.getValue().newInstance();
                  try (Connection connection = driver.connect(jdbcUrl.getValue(), properties))
                  {
                     if (connection == null)
                        throw new RuntimeException("JDBC URL [" + jdbcUrl.getValue()
                                 + "] is not compatible with the selected driver [" + driverClass.getValue().getName()
                                 + "].");

                     context.addValidationInformation(verifyConnection, "Connection successful.");
                  }
                  finally
                  {
                     DriverManager.deregisterDriver(driver);
                  }
               }
               catch (Exception e)
               {
                  log.log(Level.INFO, "Connection failed: " + properties, e);
                  Throwable exception = e;
                  while (exception.getCause() != null)
                  {
                     exception = exception.getCause();
                  }
                  if (exception != null)
                  {
                     if (exception instanceof UnknownHostException)
                     {
                        context.addValidationError(context.getCurrentInputComponent(),
                                 "Unknown host: " + exception.getMessage());
                     }
                     else
                     {
                        context.addValidationError(context.getCurrentInputComponent(),
                                 "Could not connect to database: " + exception.getMessage());
                     }
                  }
               }
            }
         }
      }).addValueChangeListener(new ConnectionStaleValueChangeListener());

      initializeBuilder(builder);
   }

   /**
    * @param builder
    */
   protected void initializeBuilder(UIBuilder builder)
   {
      builder.add(jdbcUrl)
               .add(userName)
               .add(userPassword)
               .add(saveUserPassword)
               .add(hibernateDialect)
               .add(driverLocation)
               .add(driverClass)
               .add(verifyConnection);
   }

   public Properties createConnectionProperties()
   {
      String driverClassProperty = driverClass.getValue() == null ? "" : driverClass.getValue().getName();
      String userNameProperty = userName.getValue() == null ? "" : userName.getValue();
      String dialectProperty = hibernateDialect.getValue() == null ? "" : hibernateDialect.getValue()
               .getClassName();
      String passwordProperty = userPassword.getValue() == null ? "" : userPassword.getValue();
      String jdbcUrlProperty = jdbcUrl.getValue() == null ? "" : jdbcUrl.getValue();

      Properties result = new Properties();

      result.setProperty("hibernate.connection.driver_class", driverClassProperty);
      result.setProperty("hibernate.connection.username", userNameProperty);
      result.setProperty("hibernate.dialect", dialectProperty);
      result.setProperty("hibernate.connection.password", passwordProperty);
      result.setProperty("hibernate.connection.url", jdbcUrlProperty);

      return result;
   }

   private final class LocateDriverClassNamesCallable implements Callable<Iterable<Class>>
   {
      @Override
      public Iterable<Class> call() throws Exception
      {
         if (driverClasses == null)
         {
            FileResource<?> resource = driverLocation.getValue();
            if (resource == null)
            {
               return Collections.emptyList();
            }
            File file = resource.getUnderlyingResourceObject();
            if (resource != null && resource.exists())
            {
               driverClasses = new ArrayList<>();
               try (JarFile jarFile = new JarFile(file);)
               {
                  URL[] urls = new URL[] { file.toURI().toURL() };
                  URLClassLoader classLoader = URLClassLoader.newInstance(urls);
                  Class<?> driverClass = classLoader.loadClass(Driver.class.getName());
                  Enumeration<JarEntry> iter = jarFile.entries();
                  while (iter.hasMoreElements())
                  {
                     JarEntry entry = iter.nextElement();
                     if (entry.getName().endsWith(".class"))
                     {
                        String name = entry.getName();
                        name = name.substring(0, name.length() - 6);
                        name = name.replace('/', '.');
                        try
                        {
                           Class<?> clazz = classLoader.loadClass(name);
                           if (driverClass.isAssignableFrom(clazz))
                           {
                              driverClasses.add(clazz);
                           }
                        }
                        catch (ClassNotFoundException | NoClassDefFoundError err)
                        {
                           // ignore
                        }
                     }
                  }
               }
               catch (Exception e)
               {
                  log.log(Level.WARNING, "Could not locate JDBC driver.", e);
               }
            }
         }
         return driverClasses;
      }
   }

   private final class DriverNamesStaleValueChangeListener implements ValueChangeListener
   {
      @Override
      public void valueChanged(ValueChangeEvent event)
      {
         driverClasses = null;
      }
   }

   private final class ConnectionStaleValueChangeListener implements ValueChangeListener
   {
      @Override
      public void valueChanged(ValueChangeEvent event)
      {
         connectionStale = true;
      }
   }

}

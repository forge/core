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

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.database.tools.jpa.HibernateDialect;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.input.ValueChangeListener;
import org.jboss.forge.addon.ui.input.events.ValueChangeEvent;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.validate.UIValidator;

public abstract class AbstractConnectionProfileDetailsPage implements UICommand
{
   private static final Logger log = Logger.getLogger(AbstractConnectionProfileDetailsPage.class.getName());

   @Inject
   @WithAttributes(label = "JDBC URL", description = "The jdbc url for the database tables", required = true)
   protected UIInput<String> jdbcUrl;

   @Inject
   @WithAttributes(label = "User Name", description = "The user name for the database connection", required = true)
   protected UIInput<String> userName;

   @Inject
   @WithAttributes(label = "User Password", description = "The password for the database connection", required = false, defaultValue = "", type = InputType.SECRET)
   protected UIInput<String> userPassword;

   @Inject
   @WithAttributes(label = "Save User Password?", description = "Should the connection password be saved?")
   protected UIInput<Boolean> saveUserPassword;

   @Inject
   @WithAttributes(label = "Hibernate Dialect", description = "The Hibernate dialect to use", required = true)
   protected UISelectOne<HibernateDialect> hibernateDialect;

   @Inject
   @WithAttributes(label = "Driver Location", description = "The location of the jar file that contains the JDBC driver", required = true)
   protected UIInput<FileResource<?>> driverLocation;

   @Inject
   @WithAttributes(label = "Driver Class", description = "The class name of the JDBC driver", required = true)
   protected UISelectOne<Class<?>> driverClass;

   @Inject
   @WithAttributes(label = "Verify Database Connection", description = "Attempt to connect to the database and verify connectivity")
   private UIInput<Boolean> verifyConnection;

   private boolean connectionStale;
   private List<Class<?>> driverClasses = null;

   public void initializeUI(UIBuilder builder) throws Exception
   {
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
      }).addValueChangeListener(
               new CompositeValueChangeListener(
                        new ConnectionStaleValueChangeListener(),
                        new DriverNamesStaleValueChangeListener())
               );

      driverClass.setValueChoices(new LocateDriverClassNamesCallable())
               .setItemLabelConverter(new Converter<Class<?>, String>()
               {
                  @Override
                  public String convert(Class<?> source)
                  {
                     if (source != null)
                        return source.getName();
                     else
                        return "";
                  }
               })
               .setDefaultValue(new Callable<Class<?>>()
               {
                  @Override
                  public Class<?> call() throws Exception
                  {
                     Class<?> result = null;
                     Iterator<Class<?>> iterator = driverClass.getValueChoices().iterator();
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

   private final class LocateDriverClassNamesCallable implements Callable<Iterable<Class<?>>>
   {
      @Override
      public Iterable<Class<?>> call() throws Exception
      {
         if (driverClasses == null)
         {
            driverClasses = new ArrayList<>();
            FileResource<?> resource = driverLocation.getValue();
            if (resource == null)
            {
               return Collections.emptyList();
            }
            File file = (File) resource.getUnderlyingResourceObject();
            if (resource != null && resource.exists())
            {
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

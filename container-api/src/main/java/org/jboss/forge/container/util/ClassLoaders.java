package org.jboss.forge.container.util;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.exception.ContainerException;

/**
 * Utility class for executing fragments of code within a specific {@link Thread#getContextClassLoader()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClassLoaders
{
   private static Logger log = Logger.getLogger(ClassLoaders.class.getName());

   /**
    * Execute the given {@link Callable} in the {@link ClassLoader} provided. Return the result, if any.
    */
   public static <T> T executeIn(ClassLoader loader, Callable<T> task)
   {
      if (task == null)
         return null;

      if (log.isLoggable(Level.FINE))
      {
         log.fine("ClassLoader [" + loader + "] task began.");
      }
      ClassLoader original = SecurityActions.getContextClassLoader();
      try
      {
         SecurityActions.setContextClassLoader(loader);
         return task.call();
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new ContainerException("Error invoking Task within ClassLoader [" + loader + "]", e);
      }
      finally
      {
         SecurityActions.setContextClassLoader(original);
         if (log.isLoggable(Level.FINE))
         {
            log.fine("ClassLoader [" + loader + "] task ended.");
         }
      }
   }

   public static boolean containsClass(ClassLoader loader, Class<?> type)
   {
      Assert.notNull(loader, "Class loader to inspect must not be null.");
      Assert.notNull(type, "Class to find must not be null.");

      try
      {
         return loader.loadClass(type.getName()).equals(type);
      }
      catch (ClassNotFoundException e)
      {
         return false;
      }
   }

   public static boolean containsClass(ClassLoader loader, String type)
   {
      Assert.notNull(loader, "Class loader to inspect must not be null.");
      Assert.notNull(type, "Class to find must not be null.");

      try
      {
         loader.loadClass(type);
         return true;
      }
      catch (ClassNotFoundException e)
      {
         return false;
      }
   }

   public static Class<?> loadClass(ClassLoader loader, Class<?> type)
   {
      Assert.notNull(loader, "Class loader to inspect must not be null.");
      Assert.notNull(type, "Class to load must not be null.");

      try
      {
         return loader.loadClass(type.getName());
      }
      catch (ClassNotFoundException e)
      {
         throw new ContainerException(e);
      }
   }
}

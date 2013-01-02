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
}

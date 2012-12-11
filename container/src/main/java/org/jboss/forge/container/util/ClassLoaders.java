package org.jboss.forge.container.util;

import java.util.concurrent.Callable;

import org.jboss.forge.container.exception.ContainerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for executing fragments of code within a specific {@link Thread#getContextClassLoader()}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClassLoaders
{
   private static Logger log = LoggerFactory.getLogger(ClassLoaders.class);

   /**
    * Execute the given {@link Callable} in the {@link ClassLoader} provided. Return the result, if any.
    */
   public static <T> T executeIn(ClassLoader loader, Callable<T> task)
   {
      log.debug("[Thread " + Thread.currentThread().getName() + "] ClassLoader ["
               + loader + "] task began.");
      ClassLoader original = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(loader);
         return task.call();
      }
      catch (Exception e)
      {
         throw new ContainerException("[Thread - " + Thread.currentThread().getName()
                  + "] Error invoking Task within ClassLoader [" + loader + "]", e);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(original);
         log.debug("[Thread " + Thread.currentThread().getName() + "] ClassLoader ["
                  + loader + "] task ended.");
      }
   }
}

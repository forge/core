package org.jboss.forge.container.util;

import java.util.concurrent.Callable;

import org.jboss.forge.container.exception.ContainerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for common {@link ClassLoader} operations.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:gegastaldi@gmail.com">George Gastaldi</a>
 */
public class ClassLoaders
{
   private static Logger log = LoggerFactory.getLogger(ClassLoaders.class);

   /**
    * Execute the given {@link Callable} in the {@link ClassLoader} provided.
    */
   public static <V> V executeIn(ClassLoader loader, Callable<V> task)
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

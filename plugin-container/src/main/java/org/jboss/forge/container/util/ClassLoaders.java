package org.jboss.forge.container.util;

import org.jboss.forge.container.exception.ContainerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * // TODO document
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClassLoaders
{
   private static Logger log = LoggerFactory.getLogger(ClassLoaders.class);

   public static interface Task
   {
      void perform() throws Exception;
   }

   /**
    * Execute the given {@link Task} in the {@link ClassLoader} provided.
    */
   public static void executeIn(ClassLoader loader, Task task)
   {log.debug("[Thread " + Thread.currentThread().getName() + "] ClassLoader ["
            + loader + "] task began.");
      ClassLoader original = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(loader);
         task.perform();
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

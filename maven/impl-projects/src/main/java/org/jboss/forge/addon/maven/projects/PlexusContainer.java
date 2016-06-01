/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.util.concurrent.Callable;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.jboss.forge.furnace.container.simple.AbstractEventListener;
import org.jboss.forge.furnace.util.ClassLoaders;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class PlexusContainer extends AbstractEventListener
{
   private org.codehaus.plexus.DefaultPlexusContainer plexusContainer;

   public <T> T lookup(final Class<T> type)
   {
      try
      {
         return ClassLoaders.executeIn(Thread.currentThread().getContextClassLoader(), new Callable<T>()
         {
            @Override
            public T call() throws Exception
            {
               return getPlexusContainer().lookup(type);
            }
         });
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not look up component of type [" + type.getName() + "]", e);
      }
   }

   @Override
   protected void handleThisPreShutdown()
   {
      try
      {
         ClassLoaders.executeIn(Thread.currentThread().getContextClassLoader(), new Callable<Void>()
         {
            @Override
            public Void call() throws Exception
            {
               if (plexusContainer != null)
               {
                  plexusContainer.dispose();
                  plexusContainer = null;
               }
               return null;
            }
         });
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error encountered while disposing PlexusContainer", e);
      }
   }

   private org.codehaus.plexus.PlexusContainer getPlexusContainer() throws Exception
   {
      if (plexusContainer == null)
      {
         plexusContainer = ClassLoaders.executeIn(Thread.currentThread().getContextClassLoader(),
                  new Callable<DefaultPlexusContainer>()
                  {

                     @Override
                     public DefaultPlexusContainer call() throws Exception
                     {
                        try
                        {
                           ContainerConfiguration config = new DefaultContainerConfiguration().setAutoWiring(true)
                                    .setClassPathScanning(PlexusConstants.SCANNING_INDEX);
                           plexusContainer = new DefaultPlexusContainer(config);
                           // NOTE: To avoid inconsistencies, we'll use the TCCL exclusively for lookups
                           plexusContainer.setLookupRealm(null);
                           ConsoleLoggerManager loggerManager = new ConsoleLoggerManager();
                           loggerManager.setThreshold("ERROR");
                           plexusContainer.setLoggerManager(loggerManager);
                           return plexusContainer;
                        }
                        catch (Exception e)
                        {
                           throw new RuntimeException(
                                    "Could not initialize Maven", e);
                        }
                     }
                  });
      }
      return plexusContainer;
   }

}

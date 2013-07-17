/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.util.concurrent.Callable;

import javax.inject.Singleton;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.jboss.forge.furnace.util.ClassLoaders;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
class PlexusContainer
{
   private org.codehaus.plexus.PlexusContainer plexusContainer;

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
                           ContainerConfiguration config = new DefaultContainerConfiguration().setAutoWiring(true);
                           plexusContainer = new DefaultPlexusContainer(config);
                           ConsoleLoggerManager loggerManager = new ConsoleLoggerManager();
                           loggerManager.setThreshold("ERROR");
                           ((DefaultPlexusContainer) plexusContainer).setLoggerManager(loggerManager);
                           return (DefaultPlexusContainer) plexusContainer;
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

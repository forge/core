package org.example;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.enterprise.event.Observes;

import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.event.PreShutdown;
import org.jboss.forge.container.services.Remote;

@Remote
public class SimpleService
{
   public void postStartup(@Observes PostStartup event) throws IOException
   {
      System.out.println("SimpleService has been started, and says hello!");

      Enumeration<URL> resources = Thread.currentThread().getContextClassLoader()
               .getResources("META-INF/services/javax.enterprise.inject.spi.Extension");
      while (resources.hasMoreElements())
      {
         System.out.println(resources.nextElement().toString());
      }

   }

   public void preShutdown(@Observes PreShutdown event)
   {
      System.out.println("SimpleService will shut down, and says goodbye!");
   }
}

package org.example;

import javax.enterprise.event.Observes;

import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.services.Remote;

@Remote
public class SimpleService
{
   public void postStartup(@Observes PostStartup event)
   {
      System.out.println("SimpleService has been started, and says hello!");
   }
}

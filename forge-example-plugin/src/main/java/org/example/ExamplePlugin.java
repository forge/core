package org.example;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.meta.PluginRegistry;
import org.jboss.forge.container.plugin.Plugin;

public class ExamplePlugin implements Plugin
{
   @Inject
   private PluginRegistry registry;
   
   public PluginRegistry getRegistry()
   {
      return registry;
   }
   
   public void postStartup(@Observes PostStartup event)
   {
      System.out.println("ExamplePlugin observed PostStartup");
   }
}

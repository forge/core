package org.example;

import org.jboss.forge.container.services.Remote;

@Remote
public class PublishedService
{
   public String getMessage()
   {
      return "Hello from PublishedService";
   }
}

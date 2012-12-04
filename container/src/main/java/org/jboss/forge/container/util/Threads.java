package org.jboss.forge.container.util;

public class Threads
{

   public static void sleep(int millis)
   {
      try
      {
         Thread.sleep(millis);
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

}

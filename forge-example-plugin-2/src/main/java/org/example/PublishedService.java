package org.example;


public class PublishedService
{
   public String getMessage()
   {
      return "A message from PublishedService [" + this.getClass().getClassLoader() + "]";
   }
}

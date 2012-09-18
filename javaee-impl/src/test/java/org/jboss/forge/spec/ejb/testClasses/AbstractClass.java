package org.jboss.forge.spec.ejb.testClasses;

public abstract class AbstractClass<T>
{

   private String getName()
   {
      return "flower";
   }

   public abstract String getHello(String name);

   protected abstract String getHelloWithNameAndSurname(String name,
            String surname);
}

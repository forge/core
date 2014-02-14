package org.jboss.forge.addon.scaffold.mock;

public class Scaffolded
{

   private String name;

   public Scaffolded(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public String toString()
   {
      return name;
   }
}

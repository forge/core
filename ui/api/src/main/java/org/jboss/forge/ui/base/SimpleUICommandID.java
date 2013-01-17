package org.jboss.forge.ui.base;

import org.jboss.forge.ui.UICommandID;

public class SimpleUICommandID implements UICommandID
{
   private final String name;
   private final String description;

   public SimpleUICommandID(String name, String description)
   {
      super();
      this.name = name;
      this.description = description;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public String getDescription()
   {
      return description;
   }
}

package org.jboss.forge.ui.base;

import org.jboss.forge.ui.UICategory;
import org.jboss.forge.ui.UICommandMetadata;

public class UICommandMetadataBase implements UICommandMetadata
{
   private final String name;
   private final String description;
   private UICategory category;

   public UICommandMetadataBase(String name, String description)
   {
      super();
      this.name = name;
      this.description = description;
   }

   public UICommandMetadataBase(String name, String description, UICategory category)
   {
      super();
      this.name = name;
      this.description = description;
      this.category = category;
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

   @Override
   public UICategory getCategory()
   {
      return category;
   }
}

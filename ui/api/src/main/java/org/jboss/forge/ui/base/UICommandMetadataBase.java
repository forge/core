package org.jboss.forge.ui.base;

import java.net.URL;

import org.jboss.forge.ui.UICategory;
import org.jboss.forge.ui.UICommandMetadata;

public class UICommandMetadataBase implements UICommandMetadata
{
   private final String name;
   private final String description;
   private final UICategory category;
   private final URL docLocation;

   public UICommandMetadataBase(String name, String description)
   {
      this(name, description, null, null);
   }

   public UICommandMetadataBase(String name, String description, UICategory category)
   {
      this(name, description, category, null);
   }

   public UICommandMetadataBase(String name, String description, URL docLocation)
   {
      this(name, description, null, docLocation);
   }

   public UICommandMetadataBase(String name, String description, UICategory category, URL docLocation)
   {
      super();
      this.name = name;
      this.description = description;
      this.category = category;
      this.docLocation = docLocation;
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

   @Override
   public URL getDocLocation()
   {
      return docLocation;
   }
}

package org.jboss.forge.ui.base;

import java.net.URL;

import org.jboss.forge.ui.UICategory;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;

public class UICommandMetadataBase implements UICommandMetadata
{

   private static String[] VALID_DOC_EXTENSIONS = { ".txt.gzip", ".txt.gz", ".txt", ".asciidoc.gzip", ".asciidoc.gz",
            ".asciidoc" };

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

   /**
    * Calculates the location of the documentation of a specific {@link UICommand}
    *
    * @param type an {@link UICommand} instance
    * @return the URL with the location for the {@link UICommand} documentation, or null if not found
    */
   public static URL getDocLocationFor(Class<? extends UICommand> type)
   {
      URL url = null;
      for (String extension : VALID_DOC_EXTENSIONS)
      {
         String docFileName = type.getSimpleName() + extension;
         url = type.getResource(docFileName);
         if (url != null)
         {
            break;
         }
      }
      return url;
   }
}

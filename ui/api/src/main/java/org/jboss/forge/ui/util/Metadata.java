/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.util;

import java.net.URL;

import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.metadata.UICategory;
import org.jboss.forge.ui.metadata.UICommandMetadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Metadata implements UICommandMetadata
{
   private static String[] VALID_DOC_EXTENSIONS = {
            ".txt.gzip",
            ".txt.gz",
            ".txt",
            ".asciidoc.gzip",
            ".asciidoc.gz",
            ".asciidoc" };

   private UICommandMetadataBase metadata = new UICommandMetadataBase();

   public static Metadata forCommand(Class<? extends UICommand> type)
   {
      return new Metadata(type);
   }

   public Metadata(Class<? extends UICommand> type)
   {
      docLocation(getDocLocationFor(type)).name(type.getName()).category(Categories.create("Uncategorized"));
   }

   public Metadata name(String name)
   {
      metadata.name = name;
      return this;
   }

   public Metadata description(String description)
   {
      metadata.description = description;
      return this;
   }

   public Metadata category(UICategory category)
   {
      metadata.category = category;
      return this;
   }

   public Metadata docLocation(URL docLocation)
   {
      metadata.docLocation = docLocation;
      return this;
   }

   private URL getDocLocationFor(Class<? extends UICommand> type)
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

   @Override
   public String getName()
   {
      return metadata.getName();
   }

   @Override
   public String getDescription()
   {
      return metadata.getDescription();
   }

   @Override
   public UICategory getCategory()
   {
      return metadata.getCategory();
   }

   @Override
   public URL getDocLocation()
   {
      return metadata.getDocLocation();
   }

   @Override
   public String toString()
   {
      return metadata.toString();
   }

   static class UICommandMetadataBase implements UICommandMetadata
   {
      protected String name;
      protected String description;
      protected UICategory category;
      protected URL docLocation;

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

      @Override
      public String toString()
      {
         return "UICommandMetadataBase {" +
                  "name: " + name +
                  ", description: " + description +
                  ", category: " + category +
                  ", docLocation: " + docLocation + "]";
      }

   }

}

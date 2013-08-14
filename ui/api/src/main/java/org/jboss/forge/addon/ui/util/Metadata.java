/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.util;

import java.net.URL;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.metadata.UICategory;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;

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

   private String name;
   private String description;
   private UICategory category;
   private URL docLocation;

   public static Metadata from(UICommandMetadata origin)
   {
      Metadata metadata = new Metadata();
      metadata.docLocation(origin.getDocLocation()).name(origin.getName()).description(origin.getDescription())
               .category(origin.getCategory());
      return metadata;
   }

   public static Metadata forCommand(Class<? extends UICommand> type)
   {
      return new Metadata(type);
   }

   private Metadata()
   {
   }

   public Metadata(Class<? extends UICommand> type)
   {
      docLocation(getDocLocationFor(type)).name(type.getName()).category(Categories.create("Uncategorized"));
   }

   public Metadata name(String name)
   {
      this.name = name;
      return this;
   }

   public Metadata description(String description)
   {
      this.description = description;
      return this;
   }

   public Metadata category(UICategory category)
   {
      this.category = category;
      return this;
   }

   public Metadata docLocation(URL docLocation)
   {
      this.docLocation = docLocation;
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

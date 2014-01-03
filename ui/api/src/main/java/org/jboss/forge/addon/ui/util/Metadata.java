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
import org.jboss.forge.furnace.util.Assert;

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

   private final Class<? extends UICommand> type;

   /**
    * Create a new {@link UICommandMetadata} implementation from the given {@link UICommandMetadata} origin, and the
    * given {@link UICommand} type.
    */
   public static Metadata from(UICommandMetadata origin, Class<? extends UICommand> type)
   {
      Assert.notNull(origin, "Parent UICommand must not be null.");
      Assert.notNull(type, "UICommand type must not be null.");
      Metadata metadata = new Metadata(type);
      metadata.docLocation(origin.getDocLocation()).name(origin.getName()).description(origin.getDescription())
               .category(origin.getCategory());
      return metadata;
   }

   /**
    * Create a new {@link UICommandMetadata} for the given {@link UICommand} type.
    */
   public static Metadata forCommand(Class<? extends UICommand> type)
   {
      Assert.notNull(type, "UICommand type must not be null.");
      return new Metadata(type);
   }

   private Metadata(Class<? extends UICommand> type)
   {
      this.type = type;
      docLocation(getDocLocationFor(type)).name(type.getName()).category(UICategory.NO_CATEGORY)
               .description(UICommandMetadata.NO_DESCRIPTION);
   }

   /**
    * Set the name for the corresponding {@link UICommand}.
    */
   public Metadata name(String name)
   {
      this.name = name;
      return this;
   }

   /**
    * Set the description for the corresponding {@link UICommand}.
    */
   public Metadata description(String description)
   {
      this.description = description;
      return this;
   }

   /**
    * Set the {@link UICategory} of the corresponding {@link UICommand}.
    */
   public Metadata category(UICategory category)
   {
      this.category = category;
      return this;
   }

   /**
    * Set the {@link URL} document location of the corresponding {@link UICommand}.
    */
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

   @Override
   public Class<? extends UICommand> getType()
   {
      return type;
   }
}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.util;

import java.net.URL;

import org.jboss.forge.addon.ui.command.UICommand;
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
            ".asciidoc",
            ".ad" };

   private String name;
   private String description;
   private String longDescription;
   private UICategory category;
   private URL docLocation;
   private boolean deprecated;
   private String deprecatedMessage;

   private final Class<?> type;

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
   public static Metadata forCommand(Class<?> type)
   {
      Assert.notNull(type, "UICommand type must not be null.");
      return new Metadata(type);
   }

   private Metadata(Class<?> type2)
   {
      this.type = type2;
      docLocation(getDocLocationFor(type2)).name(type2.getName()).category(Categories.createDefault())
               .description(UICommandMetadata.NO_DESCRIPTION).deprecated(type.getAnnotation(Deprecated.class) != null);
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
    * Set the long description for the corresponding {@link UICommand}.
    */
   public Metadata longDescription(String description)
   {
      this.longDescription = description;
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

   /**
    * Set the deprecated flag for this command
    */
   public Metadata deprecated(boolean deprecated)
   {
      this.deprecated = deprecated;
      return this;
   }

   /**
    * Set the deprecated message for this command
    */
   public Metadata deprecatedMessage(String deprecatedMessage)
   {
      this.deprecatedMessage = deprecatedMessage;
      return this;
   }

   private URL getDocLocationFor(Class<?> type)
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

   @Override public String getLongDescription() { return longDescription; }

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
      return "Metadata [name=" + name + ", description=" + description + ", category=" + category + ", docLocation="
               + docLocation + ", deprecated=" + deprecated + ", deprecatedMessage=" + deprecatedMessage + "]";
   }

   @Override
   public boolean isDeprecated()
   {
      return deprecated;
   }

   @Override
   public String getDeprecatedMessage()
   {
      return deprecatedMessage;
   }

   @Override
   public Class<?> getType()
   {
      return type;
   }
}

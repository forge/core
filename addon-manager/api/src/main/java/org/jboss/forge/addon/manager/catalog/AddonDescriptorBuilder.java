/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.catalog;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonDescriptorBuilder implements AddonDescriptor
{
   private final String id;
   private final AddonDescriptorCategory category;
   private String name;
   private String description;
   private String authorName;
   private String[] tags;
   private String[] installCmd;

   public AddonDescriptorBuilder(String id, AddonDescriptorCategory category)
   {
      this.id = id;
      this.category = category;
   }

   public AddonDescriptorBuilder name(String name)
   {
      this.name = name;
      return this;
   }

   public AddonDescriptorBuilder description(String description)
   {
      this.description = description;
      return this;
   }

   public AddonDescriptorBuilder authorName(String authorName)
   {
      this.authorName = authorName;
      return this;
   }

   public AddonDescriptorBuilder tags(String... tags)
   {
      this.tags = tags;
      return this;
   }

   public AddonDescriptorBuilder installCmd(String... installCmd)
   {
      this.installCmd = installCmd;
      return this;
   }

   @Override
   public String getId()
   {
      return id;
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
   public AddonDescriptorCategory getCategory()
   {
      return category;
   }

   @Override
   public String[] getTags()
   {
      return tags;
   }

   @Override
   public String[] getInstallCmd()
   {
      return installCmd;
   }

   @Override
   public String getAuthorName()
   {
      return authorName;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AddonDescriptorBuilder other = (AddonDescriptorBuilder) obj;
      if (id == null)
      {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return getId() + ":" + getName();
   }
}
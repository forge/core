/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project.packaging;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public enum PackagingType
{
   NONE("", "None"),
   BASIC("pom", "Basic Project"),
   JAR("jar", "Java Application"),
   WAR("war", "Java Web Application"),
   EAR("ear", "Java Enterprise Application"),
   BUNDLE("bundle", "OSGI Bundle Project"),
   OTHER("", "Other packaging type");

   private String type;
   private String description;

   private PackagingType(final String type, final String description)
   {
      setType(type);
      setDescription(description);
   }

   public String getType()
   {
      return type;
   }

   private void setType(String type)
   {
      if (type != null)
      {
         type = type.trim().toLowerCase();
      }
      this.type = type;
   }

   @Override
   public String toString()
   {
      return type;
   }

   public String getDescription()
   {
      return description;
   }

   private void setDescription(final String description)
   {
      this.description = description;
   }

   public static PackagingType from(String type)
   {
      PackagingType result = OTHER;
      if ((type != null) && !type.trim().isEmpty())
      {
         type = type.trim();
         for (PackagingType p : values())
         {
            if (p.getType().equals(type) || p.name().equalsIgnoreCase(type))
            {
               result = p;
               break;
            }
         }
      }
      return result;
   }
}

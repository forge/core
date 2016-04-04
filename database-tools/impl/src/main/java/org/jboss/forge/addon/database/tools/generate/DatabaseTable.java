/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.database.tools.generate;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public final class DatabaseTable
{
   private final String catalog;
   private final String schema;
   private final String name;

   /**
    * @param catalog
    * @param schema
    * @param name
    */
   public DatabaseTable(String catalog, String schema, String name)
   {
      super();
      this.catalog = catalog;
      this.schema = schema;
      this.name = name;
   }

   /**
    * @return the catalog
    */
   public String getCatalog()
   {
      return catalog;
   }

   /**
    * @return the schema
    */
   public String getSchema()
   {
      return schema;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((schema == null) ? 0 : schema.hashCode());
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
      DatabaseTable other = (DatabaseTable) obj;
      if (catalog == null)
      {
         if (other.catalog != null)
            return false;
      }
      else if (!catalog.equals(other.catalog))
         return false;
      if (name == null)
      {
         if (other.name != null)
            return false;
      }
      else if (!name.equals(other.name))
         return false;
      if (schema == null)
      {
         if (other.schema != null)
            return false;
      }
      else if (!schema.equals(other.schema))
         return false;
      return true;
   }

   @Override
   public String toString()
   {
      return "DatabaseTable [catalog=" + catalog + ", schema=" + schema + ", name=" + name + "]";
   }
}

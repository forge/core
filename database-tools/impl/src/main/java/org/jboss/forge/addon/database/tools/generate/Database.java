/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.database.tools.generate;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.roaster.model.util.Strings;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class Database
{
   private final String catalog;
   private final String schema;
   private final List<DatabaseTable> tables = new ArrayList<>();

   public Database(String catalog, String schema)
   {
      this.catalog = catalog;
      this.schema = schema;
   }

   public void addTable(String catalog, String schema, String name)
   {
      tables.add(new DatabaseTable(catalog, schema, name));
   }

   public void addTable(DatabaseTable table)
   {
      tables.add(table);
   }

   /**
    * @return the tables
    */
   public List<DatabaseTable> getTables()
   {
      return tables;
   }

   /**
    * @return the schema
    */
   public String getSchema()
   {
      return schema;
   }

   /**
    * @return the catalog
    */
   public String getCatalog()
   {
      return catalog;
   }

   public boolean isCatalogSet()
   {
      return !Strings.isNullOrEmpty(catalog);
   }

   public boolean isSchemaSet()
   {
      return !Strings.isNullOrEmpty(schema);
   }
}

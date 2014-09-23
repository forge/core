/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import org.jboss.forge.furnace.util.Strings;

/**
 * Represents a complete JPA data-source configuration.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JPADataSource
{
   private String jdbcDriver;
   private String databaseURL;
   private String username;
   private String password;
   private DatabaseType database;
   private String jndiDataSource;
   private PersistenceContainer container;
   private PersistenceProvider provider;
   private SchemaGenerationType schemaGenerationType = SchemaGenerationType.DROP_CREATE;

   public DatabaseType getDatabase()
   {
      return database == null ? DatabaseType.DEFAULT : database;
   }

   public String getJndiDataSource()
   {
      return jndiDataSource;
   }

   public String getJdbcDriver()
   {
      return jdbcDriver;
   }

   public String getDatabaseURL()
   {
      return databaseURL;
   }

   public String getUsername()
   {
      return username;
   }

   public String getPassword()
   {
      return password;
   }

   public JPADataSource setDatabase(final DatabaseType database)
   {
      this.database = database;
      return this;
   }

   public JPADataSource setJndiDataSource(final String jtaDataSource)
   {
      this.jndiDataSource = jtaDataSource;
      return this;
   }

   public JPADataSource setDatabaseType(final DatabaseType databaseType)
   {
      this.database = databaseType;
      return this;
   }

   public JPADataSource setJdbcDriver(final String jdbcDriver)
   {
      this.jdbcDriver = jdbcDriver;
      return this;
   }

   public JPADataSource setDatabaseURL(final String databaseURL)
   {
      this.databaseURL = databaseURL;
      return this;
   }

   public JPADataSource setUsername(final String username)
   {
      this.username = username;
      return this;
   }

   public JPADataSource setPassword(final String password)
   {
      this.password = password;
      return this;
   }

   public boolean hasNonDefaultDatabase()
   {
      return !DatabaseType.DEFAULT.equals(getDatabase());
   }

   public boolean hasJdbcConnectionInfo()
   {
      return !Strings.isNullOrEmpty(databaseURL)
               || !Strings.isNullOrEmpty(jdbcDriver)
               || !Strings.isNullOrEmpty(username)
               || !Strings.isNullOrEmpty(password);
   }

   public String getJdbcConnectionInfo()
   {
      String result = jdbcDriver == null ? "" : jdbcDriver;
      result += databaseURL == null ? "" : (", " + databaseURL);
      result += username == null ? "" : (", " + username);
      result += password == null ? "" : (", " + password);
      return result;
   }

   public JPADataSource setContainer(final PersistenceContainer container)
   {
      this.container = container;
      return this;
   }

   public PersistenceContainer getContainer()
   {
      return container;
   }

   public JPADataSource setProvider(final PersistenceProvider provider)
   {
      this.provider = provider;
      return this;
   }

   public JPADataSource setSchemaGenerationType(SchemaGenerationType schemaGenerationType)
   {
      this.schemaGenerationType = schemaGenerationType;
      return this;
   }

   public PersistenceProvider getProvider()
   {
      return provider;
   }

   public SchemaGenerationType getSchemaGenerationType()
   {
      return schemaGenerationType;
   }

   public void validate() throws Exception
   {
      getContainer().validate(this);
      getProvider().validate(this);
   }
}

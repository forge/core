/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee.jpa.api;

import org.jboss.forge.parser.java.util.Strings;

/**
 * Represents a complete JPA data-source configuration.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

   public PersistenceProvider getProvider()
   {
      return provider;
   }
}

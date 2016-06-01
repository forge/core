/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.providers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.javaee.jpa.DatabaseType;
import org.jboss.forge.addon.javaee.jpa.JPADataSource;
import org.jboss.forge.addon.javaee.jpa.MetaModelProvider;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;
import org.jboss.shrinkwrap.descriptor.api.persistence.PropertyCommon;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class OpenJPAProvider implements PersistenceProvider
{
   public static final String JPA_PROVIDER = "org.apache.openjpa.persistence.PersistenceProviderImpl";

   private static Map<DatabaseType, String> DICTIONARY = new HashMap<>();

   static
   {
      DICTIONARY.put(DatabaseType.ACCESS, "access");
      DICTIONARY.put(DatabaseType.DERBY, "derby");
      DICTIONARY.put(DatabaseType.DB2, "db2");
      DICTIONARY.put(DatabaseType.DB2_AS400, "db2");
      DICTIONARY.put(DatabaseType.DB2_OS390, "db2");
      DICTIONARY.put(DatabaseType.POSTGRES, "postgres");
      DICTIONARY.put(DatabaseType.MYSQL, "mysql");
      DICTIONARY.put(DatabaseType.MYSQL_INNODB, "mysql");
      DICTIONARY.put(DatabaseType.MYSQL_ISAM, "mysql");
      DICTIONARY.put(DatabaseType.ORACLE, "oracle");
      DICTIONARY.put(DatabaseType.ORACLE_9I, "oracle");
      DICTIONARY.put(DatabaseType.ORACLE_10G, "oracle");
      DICTIONARY.put(DatabaseType.ORACLE_11G, "oracle");
      DICTIONARY.put(DatabaseType.SYBASE, "sybase");
      DICTIONARY.put(DatabaseType.SYBASE_ANYWHERE, "sybase");
      DICTIONARY.put(DatabaseType.SQL_SERVER, "sqlserver");
      DICTIONARY.put(DatabaseType.SAP_DB, null);
      DICTIONARY.put(DatabaseType.INFORMIX, "informix");
      DICTIONARY.put(DatabaseType.HSQLDB, "hsql");
      DICTIONARY.put(DatabaseType.HSQLDB_IN_MEMORY, "hsql");
      DICTIONARY.put(DatabaseType.H2, "");
      DICTIONARY.put(DatabaseType.INGRES, null);
      DICTIONARY.put(DatabaseType.PROGRESS, null);
      DICTIONARY.put(DatabaseType.MCKOI, null);
      DICTIONARY.put(DatabaseType.INTERBASE, "");
      DICTIONARY.put(DatabaseType.POINTBASE, "pointbase");
      DICTIONARY.put(DatabaseType.FRONTBASE, "");
      DICTIONARY.put(DatabaseType.FIREBIRD, "");
   }

   @Override
   @SuppressWarnings("rawtypes")
   public PersistenceUnitCommon configure(PersistenceUnitCommon unit, JPADataSource ds, Project project)
   {
      unit.excludeUnlistedClasses(Boolean.FALSE);

      if (!DatabaseType.DEFAULT.equals(ds.getDatabase()))
      {
         PropertyCommon dictProperty = unit.getOrCreateProperties()
                  .createProperty();
         String dialect = DICTIONARY.get(ds.getDatabase());
         dictProperty.name("openjpa.jdbc.DBDictionary").value(dialect);
      }

      return unit;
   }

   @Override
   public void validate(JPADataSource ds) throws Exception
   {
      if (!DatabaseType.DEFAULT.equals(ds.getDatabase()))
      {
         String dialect = DICTIONARY.get(ds.getDatabase());
         if (dialect == null)
         {
            throw new RuntimeException("Unsupported database type for OpenJPA [" + ds.getDatabase() + "]");
         }
      }
   }

   @Override
   public String getProvider()
   {
      return JPA_PROVIDER;
   }

   @Override
   public List<Dependency> listDependencies()
   {
      return Arrays.asList((Dependency) DependencyBuilder.create("org.apache.openjpa:openjpa-all"));
   }

   @Override
   public MetaModelProvider getMetaModelProvider()
   {
      return new OpenJPAMetaModelProvider();
   }

   @Override
   public String getName()
   {
      return "OpenJPA";
   }
}
/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.provider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.spec.javaee.jpa.api.DatabaseType;
import org.jboss.forge.spec.javaee.jpa.api.JPADataSource;
import org.jboss.forge.spec.javaee.jpa.api.PersistenceProvider;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class OpenJPAProvider implements PersistenceProvider
{
   private static Map<DatabaseType, String> dictionary = new HashMap<DatabaseType, String>();

   static
   {
      dictionary.put(DatabaseType.ACCESS, "access");
      dictionary.put(DatabaseType.DERBY, "derby");
      dictionary.put(DatabaseType.DB2, "db2");
      dictionary.put(DatabaseType.DB2_AS400, "db2");
      dictionary.put(DatabaseType.DB2_OS390, "db2");
      dictionary.put(DatabaseType.POSTGRES, "postgres");
      dictionary.put(DatabaseType.MYSQL, "mysql");
      dictionary.put(DatabaseType.MYSQL_INNODB, "mysql");
      dictionary.put(DatabaseType.MYSQL_ISAM, "mysql");
      dictionary.put(DatabaseType.ORACLE, "oracle");
      dictionary.put(DatabaseType.ORACLE_9I, "oracle");
      dictionary.put(DatabaseType.ORACLE_10G, "oracle");
      dictionary.put(DatabaseType.ORACLE_11G, "oracle");
      dictionary.put(DatabaseType.SYBASE, "sybase");
      dictionary.put(DatabaseType.SYBASE_ANYWHERE, "sybase");
      dictionary.put(DatabaseType.SQL_SERVER, "sqlserver");
      dictionary.put(DatabaseType.SAP_DB, null);
      dictionary.put(DatabaseType.INFORMIX, "informix");
      dictionary.put(DatabaseType.HSQLDB, "hsql");
      dictionary.put(DatabaseType.HSQLDB_IN_MEMORY, "hsql");
      dictionary.put(DatabaseType.INGRES, null);
      dictionary.put(DatabaseType.PROGRESS, null);
      dictionary.put(DatabaseType.MCKOI, null);
      dictionary.put(DatabaseType.INTERBASE, "");
      dictionary.put(DatabaseType.POINTBASE, "pointbase");
      dictionary.put(DatabaseType.FRONTBASE, "");
      dictionary.put(DatabaseType.FIREBIRD, "");
   }

   @Override
   public PersistenceUnitDef configure(final PersistenceUnitDef unit, final JPADataSource ds)
   {
      unit.includeUnlistedClasses();

      if (!DatabaseType.DEFAULT.equals(ds.getDatabase()))
      {
         String dialect = dictionary.get(ds.getDatabase());
         if (dialect == null)
         {
            throw new RuntimeException("Unsupported database type for OpenJPA [" + ds.getDatabase() + "]");
         }
         unit.property("openjpa.jdbc.DBDictionary", dialect);
      }

      return unit;
   }

   @Override
   public String getProvider()
   {
      return "org.apache.openjpa.persistence.PersistenceProviderImpl";
   }

   @Override
   public List<Dependency> listDependencies()
   {
      return Arrays.asList((Dependency) DependencyBuilder.create("org.apache.openjpa:openjpa-all"));
   }

}

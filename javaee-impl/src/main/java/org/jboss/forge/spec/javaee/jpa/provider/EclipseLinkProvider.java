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
import org.jboss.forge.spec.javaee.jpa.api.MetaModelProvider;
import org.jboss.forge.spec.javaee.jpa.api.PersistenceProvider;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;

public class EclipseLinkProvider implements PersistenceProvider
{
   private static Map<DatabaseType, String> platforms = new HashMap<DatabaseType, String>();

   static
   {

      /*
       * TODO Add additional database types?
       * 
       * Non-Oracle Database platforms are located in org.eclipse.persistence.platform.database package and include the
       * following:
       */

      // AccessPlatform for Microsoft Access databases
      // AttunityPlatform for Attunity Connect JDBC drivers
      // CloudscapePlatform
      // DBasePlatform
      // JavaDBPlatform
      // TimesTen7Platform for TimesTen 7 database

      platforms.put(DatabaseType.ACCESS, "org.eclipse.persistence.platform.database.AccessPlatform");
      platforms.put(DatabaseType.DERBY, "org.eclipse.persistence.platform.database.DerbyPlatform");
      platforms.put(DatabaseType.DB2, "org.eclipse.persistence.platform.database.DB2Platform");
      platforms.put(DatabaseType.DB2_AS400, "org.eclipse.persistence.platform.database.DB2MainframePlatform");
      platforms.put(DatabaseType.DB2_OS390, "org.eclipse.persistence.platform.database.DB2MainframePlatform");
      platforms.put(DatabaseType.POSTGRES, "org.eclipse.persistence.platform.database.PostgreSQLPlatform");
      platforms.put(DatabaseType.MYSQL, "org.eclipse.persistence.platform.database.MySQLPlatform");
      platforms.put(DatabaseType.MYSQL_INNODB, "org.eclipse.persistence.platform.database.MySQLPlatform");
      platforms.put(DatabaseType.MYSQL_ISAM, "org.eclipse.persistence.platform.database.MySQLPlatform");
      platforms.put(DatabaseType.ORACLE, "org.eclipse.persistence.platform.database.oracle.OraclePlatform");
      platforms.put(DatabaseType.ORACLE_9I, "org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
      platforms.put(DatabaseType.ORACLE_10G, "org.eclipse.persistence.platform.database.oracle.Oracle10Platform");
      platforms.put(DatabaseType.ORACLE_11G, "org.eclipse.persistence.platform.database.oracle.Oracle11Platform");
      platforms.put(DatabaseType.SYBASE, "org.eclipse.persistence.platform.database.SybasePlatform");
      platforms.put(DatabaseType.SYBASE_ANYWHERE, "org.eclipse.persistence.platform.database.SQLAnyWherePlatform");
      platforms.put(DatabaseType.SQL_SERVER, "org.eclipse.persistence.platform.database.SQLServerPlatform");
      platforms.put(DatabaseType.SAP_DB, null);
      platforms.put(DatabaseType.INFORMIX, "org.eclipse.persistence.platform.database.InformixPlatform");
      platforms.put(DatabaseType.HSQLDB, "org.eclipse.persistence.platform.database.HSQLPlatform");
      platforms.put(DatabaseType.HSQLDB_IN_MEMORY, "org.eclipse.persistence.platform.database.HSQLPlatform");
      platforms.put(DatabaseType.INGRES, null);
      platforms.put(DatabaseType.PROGRESS, null);
      platforms.put(DatabaseType.MCKOI, null);
      platforms.put(DatabaseType.INTERBASE, null);
      platforms.put(DatabaseType.POINTBASE, "org.eclipse.persistence.platform.database.PointBasePlatform");
      platforms.put(DatabaseType.FRONTBASE, null);
      platforms.put(DatabaseType.FIREBIRD, null);
   }

   @Override
   public PersistenceUnitDef configure(final PersistenceUnitDef unit, final JPADataSource ds)
   {
      unit.includeUnlistedClasses();
      unit.property("eclipselink.ddl-generation", "drop-and-create-tables");

      if (!DatabaseType.DEFAULT.equals(ds.getDatabase()))
      {
         String platform = platforms.get(ds.getDatabase());
         if (platform == null)
         {
            throw new RuntimeException("Unsupported database type for Eclipselink [" + ds.getDatabase() + "]");
         }
         unit.property("eclipselink.target-database", platform);
      }

      return unit;
   }

   @Override
   public String getProvider()
   {
      return "org.eclipse.persistence.jpa.PersistenceProvider";
   }

   @Override
   public List<Dependency> listDependencies()
   {
      return Arrays.asList((Dependency) DependencyBuilder.create("org.eclipse.persistence:eclipselink"),
               (Dependency) DependencyBuilder.create("org.eclipse.persistence:javax.persistence"));
   }

   @Override
   public MetaModelProvider getMetaModelProvider()
   {
      return new EclipseLinkMetaModelProvider();
   }

}

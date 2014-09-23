/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.javaee.jpa.SchemaGenerationType;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;
import org.jboss.shrinkwrap.descriptor.api.persistence.PropertiesCommon;

public class EclipseLinkProvider implements PersistenceProvider
{
   public static final String JPA_PROVIDER = "org.eclipse.persistence.jpa.PersistenceProvider";

   private static Map<DatabaseType, String> PLATFORMS = new HashMap<>();

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

      PLATFORMS.put(DatabaseType.ACCESS, "org.eclipse.persistence.platform.database.AccessPlatform");
      PLATFORMS.put(DatabaseType.DERBY, "org.eclipse.persistence.platform.database.DerbyPlatform");
      PLATFORMS.put(DatabaseType.DB2, "org.eclipse.persistence.platform.database.DB2Platform");
      PLATFORMS.put(DatabaseType.DB2_AS400, "org.eclipse.persistence.platform.database.DB2MainframePlatform");
      PLATFORMS.put(DatabaseType.DB2_OS390, "org.eclipse.persistence.platform.database.DB2MainframePlatform");
      PLATFORMS.put(DatabaseType.POSTGRES, "org.eclipse.persistence.platform.database.PostgreSQLPlatform");
      PLATFORMS.put(DatabaseType.MYSQL, "org.eclipse.persistence.platform.database.MySQLPlatform");
      PLATFORMS.put(DatabaseType.MYSQL_INNODB, "org.eclipse.persistence.platform.database.MySQLPlatform");
      PLATFORMS.put(DatabaseType.MYSQL_ISAM, "org.eclipse.persistence.platform.database.MySQLPlatform");
      PLATFORMS.put(DatabaseType.ORACLE, "org.eclipse.persistence.platform.database.oracle.OraclePlatform");
      PLATFORMS.put(DatabaseType.ORACLE_9I, "org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
      PLATFORMS.put(DatabaseType.ORACLE_10G, "org.eclipse.persistence.platform.database.oracle.Oracle10Platform");
      PLATFORMS.put(DatabaseType.ORACLE_11G, "org.eclipse.persistence.platform.database.oracle.Oracle11Platform");
      PLATFORMS.put(DatabaseType.SYBASE, "org.eclipse.persistence.platform.database.SybasePlatform");
      PLATFORMS.put(DatabaseType.SYBASE_ANYWHERE, "org.eclipse.persistence.platform.database.SQLAnyWherePlatform");
      PLATFORMS.put(DatabaseType.SQL_SERVER, "org.eclipse.persistence.platform.database.SQLServerPlatform");
      PLATFORMS.put(DatabaseType.SAP_DB, null);
      PLATFORMS.put(DatabaseType.INFORMIX, "org.eclipse.persistence.platform.database.InformixPlatform");
      PLATFORMS.put(DatabaseType.HSQLDB, "org.eclipse.persistence.platform.database.HSQLPlatform");
      PLATFORMS.put(DatabaseType.HSQLDB_IN_MEMORY, "org.eclipse.persistence.platform.database.HSQLPlatform");
      PLATFORMS.put(DatabaseType.H2, "org.eclipse.persistence.platform.database.H2Platform");
      PLATFORMS.put(DatabaseType.INGRES, null);
      PLATFORMS.put(DatabaseType.PROGRESS, null);
      PLATFORMS.put(DatabaseType.MCKOI, null);
      PLATFORMS.put(DatabaseType.INTERBASE, null);
      PLATFORMS.put(DatabaseType.POINTBASE, "org.eclipse.persistence.platform.database.PointBasePlatform");
      PLATFORMS.put(DatabaseType.FRONTBASE, null);
      PLATFORMS.put(DatabaseType.FIREBIRD, null);
   }

   @SuppressWarnings("rawtypes")
   @Override
   public PersistenceUnitCommon configure(PersistenceUnitCommon unit, JPADataSource ds, Project project)
   {
      unit.excludeUnlistedClasses(Boolean.FALSE);
      PropertiesCommon properties = unit.getOrCreateProperties();
      String schemaGenerationPropertyValue = getSchemaGenerationPropertyValue(ds.getSchemaGenerationType());
      if (!Strings.isNullOrEmpty(schemaGenerationPropertyValue))
      {
         properties.createProperty().name("eclipselink.ddl-generation").value(schemaGenerationPropertyValue);
      }

      if (!DatabaseType.DEFAULT.equals(ds.getDatabase()))
      {
         String platform = PLATFORMS.get(ds.getDatabase());
         properties.createProperty().name("eclipselink.target-database").value(platform);
      }
      return unit;
   }

   /**
    * @see http://www.eclipse.org/eclipselink/documentation/2.4/jpa/extensions/p_ddl_generation.htm
    */
   private String getSchemaGenerationPropertyValue(SchemaGenerationType gen)
   {
      if (gen == null)
         return null;
      switch (gen)
      {
      case DROP_CREATE:
         return "drop-and-create-tables";
      case CREATE:
         return "create-or-extend-tables";
      case DROP:
      case NONE:
      default:
         return null;
      }
   }

   @Override
   public void validate(JPADataSource ds) throws Exception
   {
      if (!DatabaseType.DEFAULT.equals(ds.getDatabase()))
      {
         String platform = PLATFORMS.get(ds.getDatabase());
         if (platform == null)
         {
            throw new RuntimeException("Unsupported database type for Eclipselink [" + ds.getDatabase() + "]");
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
      return Arrays.asList((Dependency) DependencyBuilder.create("org.eclipse.persistence:eclipselink"),
               (Dependency) DependencyBuilder.create("org.eclipse.persistence:javax.persistence"));
   }

   @Override
   public MetaModelProvider getMetaModelProvider()
   {
      return new EclipseLinkMetaModelProvider();
   }

   @Override
   public String getName()
   {
      return "Eclipse Link";
   }
}

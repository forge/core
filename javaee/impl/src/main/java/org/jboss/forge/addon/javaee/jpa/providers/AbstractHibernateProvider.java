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
import org.jboss.forge.addon.javaee.jpa.SchemaGenerationType;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;
import org.jboss.shrinkwrap.descriptor.api.persistence.PropertiesCommon;

/**
 * Abstract class for the Hibernate providers
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractHibernateProvider implements PersistenceProvider
{
   private static Map<DatabaseType, String> DIALECTS = new HashMap<>();

   static
   {
      DIALECTS.put(DatabaseType.DERBY, "org.hibernate.dialect.DerbyDialect");
      DIALECTS.put(DatabaseType.DB2, "org.hibernate.dialect.DB2Dialect");
      DIALECTS.put(DatabaseType.DB2_AS400, "org.hibernate.dialect.DB2400Dialect");
      DIALECTS.put(DatabaseType.DB2_OS390, "org.hibernate.dialect.DB2390Dialect");
      DIALECTS.put(DatabaseType.POSTGRES, "org.hibernate.dialect.PostgreSQLDialect");
      DIALECTS.put(DatabaseType.MYSQL, "org.hibernate.dialect.MySQLDialect");
      DIALECTS.put(DatabaseType.MYSQL5_INNODB, "org.hibernate.dialect.MySQL5InnoDBDialect");
      DIALECTS.put(DatabaseType.MYSQL5_ISAM, "org.hibernate.dialect.MySQL5MyISAMDialect");
      DIALECTS.put(DatabaseType.MYSQL_INNODB, "org.hibernate.dialect.MySQLInnoDBDialect");
      DIALECTS.put(DatabaseType.MYSQL_ISAM, "org.hibernate.dialect.MySQLMyISAMDialect");
      DIALECTS.put(DatabaseType.ORACLE, "org.hibernate.dialect.OracleDialect");
      DIALECTS.put(DatabaseType.ORACLE_9I, "org.hibernate.dialect.Oracle9iDialect");
      DIALECTS.put(DatabaseType.ORACLE_10G, "org.hibernate.dialect.Oracle10gDialect");
      DIALECTS.put(DatabaseType.ORACLE_11G, "org.hibernate.dialect.OracleDialect");
      DIALECTS.put(DatabaseType.SYBASE, "org.hibernate.dialect.SybaseDialect");
      DIALECTS.put(DatabaseType.SYBASE_ANYWHERE, "org.hibernate.dialect.SybaseAnywhereDialect");
      DIALECTS.put(DatabaseType.SQL_SERVER, "org.hibernate.dialect.SQLServerDialect");
      DIALECTS.put(DatabaseType.SAP_DB, "org.hibernate.dialect.SAPDBDialect");
      DIALECTS.put(DatabaseType.INFORMIX, "org.hibernate.dialect.InformixDialect");
      DIALECTS.put(DatabaseType.HSQLDB, "org.hibernate.dialect.HSQLDialect");
      DIALECTS.put(DatabaseType.HSQLDB_IN_MEMORY, "org.hibernate.dialect.HSQLDialect");
      DIALECTS.put(DatabaseType.H2, "org.hibernate.dialect.H2Dialect");
      DIALECTS.put(DatabaseType.INGRES, "org.hibernate.dialect.IngresDialect");
      DIALECTS.put(DatabaseType.PROGRESS, "org.hibernate.dialect.ProgressDialect");
      DIALECTS.put(DatabaseType.MCKOI, "org.hibernate.dialect.MckoiDialect");
      DIALECTS.put(DatabaseType.INTERBASE, "org.hibernate.dialect.InterbaseDialect");
      DIALECTS.put(DatabaseType.POINTBASE, "org.hibernate.dialect.PointbaseDialect");
      DIALECTS.put(DatabaseType.FRONTBASE, "org.hibernate.dialect.FrontbaseDialect");
      DIALECTS.put(DatabaseType.FIREBIRD, "org.hibernate.dialect.FirebirdDialect");
   }

   @Override
   @SuppressWarnings("rawtypes")
   public PersistenceUnitCommon configure(PersistenceUnitCommon unit, JPADataSource ds, Project project)
   {
      unit.excludeUnlistedClasses(Boolean.FALSE);
      PropertiesCommon properties = unit.getOrCreateProperties();
      String schemaGenerationPropertyValue = getSchemaGenerationPropertyValue(ds.getSchemaGenerationType());
      if (!Strings.isNullOrEmpty(schemaGenerationPropertyValue))
      {
         properties.createProperty().name("hibernate.hbm2ddl.auto").value(schemaGenerationPropertyValue);
      }
      properties.createProperty().name("hibernate.show_sql").value("true");
      properties.createProperty().name("hibernate.format_sql").value("true");
      properties.createProperty().name("hibernate.transaction.flush_before_completion").value("true");

      if (!DatabaseType.DEFAULT.equals(ds.getDatabase()))
      {
         String dialect = getDialectFor(ds.getDatabase());
         properties.createProperty().name("hibernate.dialect").value(dialect);
      }

      return unit;
   }

   /**
    * @see https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/session-configuration.html
    */
   protected String getSchemaGenerationPropertyValue(SchemaGenerationType gen)
   {
      if (gen == null)
         return null;
      switch (gen)
      {
      case DROP_CREATE:
         return "create-drop";
      case CREATE:
         return "create";
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
         String dialect = getDialectFor(ds.getDatabase());
         if (dialect == null)
         {
            throw new RuntimeException("Unsupported database type for Hibernate [" + ds.getDatabase() + "]");
         }
      }
   }

   @Override
   public List<Dependency> listDependencies()
   {
      return Arrays.asList((Dependency) DependencyBuilder.create("org.hibernate:hibernate-entitymanager"));
   }

   @Override
   public MetaModelProvider getMetaModelProvider()
   {
      return new HibernateMetaModelProvider();
   }

   protected String getDialectFor(DatabaseType databaseType)
   {
      return DIALECTS.get(databaseType);
   }
}

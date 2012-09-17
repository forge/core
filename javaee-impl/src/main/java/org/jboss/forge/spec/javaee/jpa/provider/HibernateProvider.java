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

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HibernateProvider implements PersistenceProvider
{
   private static Map<DatabaseType, String> dialects = new HashMap<DatabaseType, String>();

   static
   {
      dialects.put(DatabaseType.DERBY, "org.hibernate.dialect.DerbyDialect");
      dialects.put(DatabaseType.DB2, "org.hibernate.dialect.DB2Dialect");
      dialects.put(DatabaseType.DB2_AS400, "org.hibernate.dialect.DB2400Dialect");
      dialects.put(DatabaseType.DB2_OS390, "org.hibernate.dialect.DB2390Dialect");
      dialects.put(DatabaseType.POSTGRES, "org.hibernate.dialect.PostgreSQLDialect");
      dialects.put(DatabaseType.MYSQL, "org.hibernate.dialect.MySQLDialect");
      dialects.put(DatabaseType.MYSQL5_INNODB, "org.hibernate.dialect.MySQL5InnoDBDialect");
      dialects.put(DatabaseType.MYSQL5_ISAM, "org.hibernate.dialect.MySQL5MyISAMDialect");
      dialects.put(DatabaseType.MYSQL_INNODB, "org.hibernate.dialect.MySQLInnoDBDialect");
      dialects.put(DatabaseType.MYSQL_ISAM, "org.hibernate.dialect.MySQLMyISAMDialect");
      dialects.put(DatabaseType.ORACLE, "org.hibernate.dialect.OracleDialect");
      dialects.put(DatabaseType.ORACLE_9I, "org.hibernate.dialect.Oracle9iDialect");
      dialects.put(DatabaseType.ORACLE_10G, "org.hibernate.dialect.Oracle10gDialect");
      dialects.put(DatabaseType.ORACLE_11G, "org.hibernate.dialect.OracleDialect");
      dialects.put(DatabaseType.SYBASE, "org.hibernate.dialect.SybaseDialect");
      dialects.put(DatabaseType.SYBASE_ANYWHERE, "org.hibernate.dialect.SybaseAnywhereDialect");
      dialects.put(DatabaseType.SQL_SERVER, "org.hibernate.dialect.SQLServerDialect");
      dialects.put(DatabaseType.SAP_DB, "org.hibernate.dialect.SAPDBDialect");
      dialects.put(DatabaseType.INFORMIX, "org.hibernate.dialect.InformixDialect");
      dialects.put(DatabaseType.HSQLDB, "org.hibernate.dialect.HSQLDialect");
      dialects.put(DatabaseType.HSQLDB_IN_MEMORY, "org.hibernate.dialect.HSQLDialect");
      dialects.put(DatabaseType.INGRES, "org.hibernate.dialect.IngresDialect");
      dialects.put(DatabaseType.PROGRESS, "org.hibernate.dialect.ProgressDialect");
      dialects.put(DatabaseType.MCKOI, "org.hibernate.dialect.MckoiDialect");
      dialects.put(DatabaseType.INTERBASE, "org.hibernate.dialect.InterbaseDialect");
      dialects.put(DatabaseType.POINTBASE, "org.hibernate.dialect.PointbaseDialect");
      dialects.put(DatabaseType.FRONTBASE, "org.hibernate.dialect.FrontbaseDialect");
      dialects.put(DatabaseType.FIREBIRD, "org.hibernate.dialect.FirebirdDialect");
   }

   @Override
   public PersistenceUnitDef configure(final PersistenceUnitDef unit, final JPADataSource ds)
   {
      unit.includeUnlistedClasses();
      unit.property("hibernate.hbm2ddl.auto", "create-drop");
      unit.property("hibernate.show_sql", "true");
      unit.property("hibernate.format_sql", "true");
      unit.property("hibernate.transaction.flush_before_completion", "true");

      if (!DatabaseType.DEFAULT.equals(ds.getDatabase()))
      {
         String dialect = dialects.get(ds.getDatabase());
         if (dialect == null)
         {
            throw new RuntimeException("Unsupported database type for Hibernate [" + ds.getDatabase() + "]");
         }
         unit.property("hibernate.dialect", dialect);
      }

      return unit;
   }

   @Override
   public String getProvider()
   {
      return "org.hibernate.ejb.HibernatePersistence";
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
}

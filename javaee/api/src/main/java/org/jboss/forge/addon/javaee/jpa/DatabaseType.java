/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;

/**
 * Relational database instance types.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public enum DatabaseType
{
   // @formatter:off
   MYSQL("mysql","mysql-connector-java"),
   ORACLE,
   DERBY("org.apache.derby","derby"),
   DB2,
   POSTGRES("postgresql","postgresql"),
   DEFAULT,
   DB2_AS400,
   DB2_OS390,
   MYSQL5_INNODB("mysql","mysql-connector-java"),
   MYSQL_INNODB("mysql","mysql-connector-java"),
   MYSQL5_ISAM("mysql","mysql-connector-java"),
   MYSQL_ISAM("mysql","mysql-connector-java"),
   ORACLE_9I,
   ORACLE_10G,
   SYBASE,
   SYBASE_ANYWHERE,
   SQL_SERVER,
   SAP_DB,
   INFORMIX,
   HSQLDB("org.hsqldb","hsqldb"),
   H2("com.h2database","h2"),
   INGRES,
   PROGRESS,
   MCKOI("com.mckoi","mckoisqldb"),
   INTERBASE,
   POINTBASE,
   FRONTBASE,
   FIREBIRD("org.firebirdsql.jdbc","jaybird-jdk18"),
   HSQLDB_IN_MEMORY, 
   ORACLE_11G, 
   ACCESS;
   // @formatter:on

   private final Coordinate driverCoordinate;

   private DatabaseType()
   {
      this.driverCoordinate = null;
   }

   private DatabaseType(String groupId, String artifactId)
   {
      this.driverCoordinate = CoordinateBuilder.create().setGroupId(groupId).setArtifactId(artifactId);
   }

   public Coordinate getDriverCoordinate()
   {
      return driverCoordinate;
   }

   public boolean isDriverCoordinateSet()
   {
      return this.driverCoordinate != null;
   }

   public static List<DatabaseType> getTypesWithDriverSet()
   {
      return Arrays.asList(DatabaseType.values())
               .stream()
               .filter(DatabaseType::isDriverCoordinateSet)
               .collect(Collectors.toList());
   }
}

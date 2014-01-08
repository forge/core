package org.jboss.forge.addon.database.tools.connections;

public enum HibernateDialect {
	
	MYSQL5("MySQL5", "org.hibernate.dialect.MySQL5Dialect"),
	MYSQL5_WITH_INNODB("MySQL5 with InnoDB", "org.hibernate.dialect.MySQL5InnoDBDialect"),
	MYSQL_WITH_MYISAM("MySQL with MyISAM", "org.hibernate.dialect.MySQLMyISAMDialect"),
	ORACLE_ANY_VERSION("Oracle (any version)", "org.hibernate.dialect.OracleDialect"),
	ORACLE_9I("Oracle 9i", "org.hibernate.dialect.Oracle9iDialect"),
	ORACLE_10G("Oracle 10g", "org.hibernate.dialect.Oracle10gDialect"),
	ORACLE_11G("Oracle 11g", "org.hibernate.dialect.Oracle10gDialect"),
	DB2("DB2", "org.hibernate.dialect.DB2Dialect"),
	DB2_AS400("DB2 AS/400", "org.hibernate.dialect.DB2400Dialect"),
	DB2_OS390("DB2 OS390", "org.hibernate.dialect.DB2390Dialect"),
	POSTGRESQL("PostgreSQL", "org.hibernate.dialect.PostgreSQLDialect"),
	MICROSOFT_SQL_SERVER_2000("Microsoft SQL Server 2000", "org.hibernate.dialect.SQLServerDialect"),
	MICROSOFT_SQL_SERVER_2005("Microsoft SQL Server 2005", "org.hibernate.dialect.SQLServer2005Dialect"),
	MICROSOFT_SQL_SERVER_2008("Microsoft SQL Server 2008", "org.hibernate.dialect.SQLServer2008Dialect"),
	SAP_DB("SAP DB", "org.hibernate.dialect.SAPDBDialect"),
	INFORMIX("Informix", "org.hibernate.dialect.InformixDialect"),
	HYPERSONIC_SQL("HypersonicSQL", "org.hibernate.dialect.HSQLDialect"),
	H2_DATABASE("H2 Database", "org.hibernate.dialect.H2Dialect"),
	INGRES("Ingres", "org.hibernate.dialect.IngresDialect"),
	PROGRESS("Progress", "org.hibernate.dialect.ProgressDialect"),
	MCKOI_SQL("Mckoi SQL", "org.hibernate.dialect.MckoiDialect"),
	INTERBASE("Interbase", "org.hibernate.dialect.InterbaseDialect"),
	POINTBASE("Pointbase", "org.hibernate.dialect.PointbaseDialect"),
	FRONTBASE("FrontBase", "org.hibernate.dialect.FrontbaseDialect"),
	FIREBIRD("Firebird", "org.hibernate.dialect.FirebirdDialect"),
	SYBASE("Sybase", "org.hibernate.dialect.SybaseASE15Dialect"),
	SYBASE_ANYWHERE("Sybase Anywhere", "org.hibernate.dialect.SybaseAnywhereDialect");
	
	private String className;
	private String databaseName;
	
	private HibernateDialect(String databaseName, String className) {
		this.className = className;
		this.databaseName = databaseName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	
	public static HibernateDialect fromClassName(String className) {
	   for (HibernateDialect dialect : HibernateDialect.values()) {
	      if (dialect.getClassName().equals(className)) {
	         return dialect;
	      }
	   }
	   return null;
	}
	
}
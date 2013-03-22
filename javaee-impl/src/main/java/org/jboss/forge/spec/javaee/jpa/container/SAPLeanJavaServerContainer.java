package org.jboss.forge.spec.javaee.jpa.container;

import javax.inject.Inject;

import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.spec.javaee.jpa.api.DatabaseType;

public class SAPLeanJavaServerContainer extends JavaEEDefaultContainer {

    private static final String DEFAULT_DATA_SOURCE = "jdbc/DefaultDB";
    
    @Inject
    private ShellPrintWriter writer;

    @Override
    protected String getDefaultDataSource() {
        return DEFAULT_DATA_SOURCE;
    }

    @Override
    protected DatabaseType getDefaultDatabaseType() {
        return DatabaseType.HSQLDB;
    }

    @Override
    protected ShellPrintWriter getWriter() {
        return writer;
    }

}

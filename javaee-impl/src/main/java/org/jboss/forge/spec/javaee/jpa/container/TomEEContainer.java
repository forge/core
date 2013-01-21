/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa.container;

import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.spec.javaee.jpa.api.DatabaseType;

import javax.inject.Inject;

public class TomEEContainer extends JavaEEDefaultContainer {
    private static final String DEFAULT_DATASOURCE_NAME = "Default JDBC Database";

    @Inject
    private ShellPrintWriter writer;

    @Override
    public ShellPrintWriter getWriter() {
        return writer;
    }

    @Override
    protected DatabaseType getDefaultDatabaseType() {
        return DatabaseType.HSQLDB;
    }

    @Override
    protected String getDefaultDataSource() {
        return DEFAULT_DATASOURCE_NAME;
    }
}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DelegatingDriver implements Driver
{
    private final Driver driver;

    public DelegatingDriver(Driver driver)
    {
        if (driver == null)
        {
            throw new IllegalArgumentException("Driver must not be null.");
        }
        this.driver = driver;
    }

    public Connection connect(String url, Properties info) throws SQLException
    {
       return driver.connect(url, info);
    }

    public boolean acceptsURL(String url) throws SQLException
    {
       return driver.acceptsURL(url);
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
    {
        return driver.getPropertyInfo(url, info);
    }

    public int getMajorVersion()
    {
        return driver.getMajorVersion();
    }

    public int getMinorVersion()
    {
        return driver.getMinorVersion();
    }

    public boolean jdbcCompliant()
    {
        return driver.jdbcCompliant();
    }

    public Logger getParentLogger()
    {
      // TODO Auto-generated method stub
       return null;
    }
}
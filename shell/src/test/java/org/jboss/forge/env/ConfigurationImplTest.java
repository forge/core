/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.env;

import javax.inject.Inject;

import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ConfigurationImplTest extends AbstractShellTest {
    private static String key = ConfigurationImplTest.class.getName() + "foo";

    @Inject
    private Configuration config;

    @Test
    public void testAccessConfigurationOutsideOfProject() throws Exception {
        getShell().setCurrentResource(createTempFolder());
        String string = config.getString(key);
        Assert.assertNull(string);

        config.setProperty(key, "bar");
        Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
        Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

        Assert.assertEquals("bar", config.getString(key));
        Assert.assertNull("bar", userConfig.getString(key));
        Assert.assertEquals("bar", projectConfig.getString(key));
    }

    @Test
    public void testSettingDefaultConfigChoosesProjectOverUser() throws Exception {
        initializeProject(PackagingType.WAR);
        String string = config.getString(key);
        Assert.assertNull(string);

        config.setProperty(key, "bar");

        /*
         * By default, the write operations will persist to the first delegate (PROJECT), if no project is available they will
         * persist to the next delegate (user settings)
         */
        Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
        Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

        Assert.assertEquals("bar", config.getString(key));
        Assert.assertNull("bar", userConfig.getString(key));
        Assert.assertEquals("bar", projectConfig.getString(key));
    }

    @Test
    public void testSettingUserConfigDirectly() throws Exception {
        Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
        initializeProject(PackagingType.JAR);
        Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

        String string = userConfig.getString(key);
        Assert.assertNull(string);
        userConfig.setProperty(key, "bar");
        Assert.assertEquals("bar", userConfig.getString(key));

        Assert.assertNull(projectConfig.getString(key));
        Assert.assertEquals("bar", config.getString(key));
    }

    @Test
    public void testProjectConfigTakesReadPriority() throws Exception {
        initializeProject(PackagingType.JAR);
        Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
        Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

        String string = userConfig.getString(key);
        Assert.assertNull(string);
        userConfig.setProperty(key, "bar");
        projectConfig.setProperty(key, "bar2");

        Assert.assertEquals("bar", userConfig.getString(key));
        Assert.assertEquals("bar2", projectConfig.getString(key));
        Assert.assertEquals("bar2", config.getString(key));
    }

    @Test
    public void testClearProjectDoesNotClearGlobal() throws Exception {
        initializeProject(PackagingType.JAR);
        Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
        Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

        String string = userConfig.getString(key);
        Assert.assertNull(string);
        userConfig.setProperty(key, "bar");
        projectConfig.setProperty(key, "bar2");

        Assert.assertEquals("bar", userConfig.getString(key));
        Assert.assertEquals("bar2", projectConfig.getString(key));
        Assert.assertEquals("bar2", config.getString(key));

        projectConfig.clearProperty(key);

        Assert.assertEquals("bar", userConfig.getString(key));
        Assert.assertNull(projectConfig.getString(key));
        Assert.assertEquals("bar", config.getString(key));
    }

    @Test
    public void testClearUserDoesNotClearProject() throws Exception {
        initializeProject(PackagingType.JAR);
        Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
        Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

        String string = userConfig.getString(key);
        Assert.assertNull(string);
        userConfig.setProperty(key, "bar");
        projectConfig.setProperty(key, "bar2");

        Assert.assertEquals("bar", userConfig.getString(key));
        Assert.assertEquals("bar2", projectConfig.getString(key));
        Assert.assertEquals("bar2", config.getString(key));

        userConfig.clearProperty(key);

        Assert.assertNull(userConfig.getString(key));
        Assert.assertEquals("bar2", projectConfig.getString(key));
        Assert.assertEquals("bar2", config.getString(key));
    }

    @Test
    public void testClearAll() throws Exception {
        initializeProject(PackagingType.JAR);
        Configuration userConfig = config.getScopedConfiguration(ConfigurationScope.USER);
        Configuration projectConfig = config.getScopedConfiguration(ConfigurationScope.PROJECT);

        String string = userConfig.getString(key);
        Assert.assertNull(string);
        userConfig.setProperty(key, "bar");
        projectConfig.setProperty(key, "bar2");

        Assert.assertEquals("bar", userConfig.getString(key));
        Assert.assertEquals("bar2", projectConfig.getString(key));
        Assert.assertEquals("bar2", config.getString(key));

        config.clearProperty(key);

        Assert.assertNull(userConfig.getString(key));
        Assert.assertNull(projectConfig.getString(key));
        Assert.assertNull(config.getString(key));
    }

    @Test
    public void testListConfig() throws Exception {
        config.setProperty(key, "OMG!");
        getShell().execute("list-config");
    }

    @After
    public void afterTestConfig() {
        config.clearProperty(key);
        config.clearProperty(key);
    }
}

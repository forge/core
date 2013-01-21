/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.mavenplugins;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.MavenPluginAdapter;
import org.junit.Test;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class MavenPluginAdapterTest {

    @Test
    public void testGetPluginConfiguration() throws Exception {
        MavenPluginAdapter adapter = new MavenPluginAdapter(createMavenPlugin());
        Configuration pluginConfiguration = adapter.getConfig();
        assertNotNull(pluginConfiguration);
    }

    @Test
    public void testCreatePluginWithExtensions() throws Exception {
        Plugin mavenPlugin = createPlugin();
        mavenPlugin.setExtensions(true);

        MavenPluginAdapter adapter = new MavenPluginAdapter(mavenPlugin);
        assertTrue(adapter.isExtensionsEnabled());
        assertTrue(adapter.isExtensions());
    }

    @Test
    public void testCreatePluginNoExtensions() throws Exception {
        Plugin mavenPlugin = createPlugin();
        mavenPlugin.setExtensions(false);

        MavenPluginAdapter adapter = new MavenPluginAdapter(mavenPlugin);
        assertFalse(adapter.isExtensionsEnabled());
        assertFalse(adapter.isExtensions());
    }

    private Plugin createPlugin() {
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setGroupId("maven-site-plugin");
        plugin.setVersion("3.0");

        return plugin;
    }


    private Plugin createMavenPlugin() throws Exception {
        Plugin plugin = createPlugin();
        Xpp3Dom dom;
        dom = Xpp3DomBuilder.build(
                new ByteArrayInputStream(
                        ("<configuration>" +
                                "   <reportPlugins>" +
                                "       <plugin>" +
                                "           <groupId>org.codehaus.mojo</groupId>" +
                                "           <artifactId>findbugs-maven-plugin</artifactId>" +
                                "           <version>2.3.2</version>" +
                                "       </plugin>" +
                                "   </reportPlugins>" +
                                "</configuration>").getBytes()),
                "UTF-8");

        plugin.setConfiguration(dom);
        return plugin;
    }


}

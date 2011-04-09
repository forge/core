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

package org.jboss.seam.forge.maven.facets;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.forge.maven.MavenCoreFacet;
import org.jboss.seam.forge.maven.MavenPluginFacet;
import org.jboss.seam.forge.maven.facets.exceptions.PluginNotFoundException;
import org.jboss.seam.forge.maven.plugins.MavenPlugin;
import org.jboss.seam.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.seam.forge.maven.util.ProjectModelTest;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.services.ProjectFactory;
import org.jboss.seam.forge.project.services.ResourceFactory;
import org.jboss.seam.forge.shell.util.ResourceUtil;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
@Singleton
@RunWith(Arquillian.class)
public class MavenPluginFacetTest extends ProjectModelTest {
    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private ResourceFactory resourceFactory;

    private static Project testProject;


    @Deployment
    public static JavaArchive getTestArchive() {
        return createTestArchive()
                .addManifestResource(
                        "META-INF/services/org.jboss.seam.forge.project.dependencies.DependencyResolverProvider");
    }

    @Before
    @Override
    public void postConstruct() throws IOException {

        project = null;
        super.postConstruct();

        if (testProject == null) {
            testProject = projectFactory.findProjectRecursively(
                    ResourceUtil.getContextDirectory(resourceFactory.getResourceFrom(new File(
                            "src/test/resources/test-pom"))));
        }
    }


    @Test
    public void testIsInstalled() throws Exception {
        boolean isInstalled = testProject.hasFacet(MavenPluginFacet.class);
        assertEquals(true, isInstalled);
    }

    @Test
    public void testListPlugins() throws Exception {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        List<MavenPlugin> mavenPlugins = mavenPluginFacet.listConfiguredPlugins();
        assertThat(mavenPlugins.size(), is(3));
    }

    @Test
    public void testAddPlugin() throws Exception {

        MavenPluginFacet mavenPluginFacet = getProject().getFacet(MavenPluginFacet.class);

        int nrOfPlugins = getNumberOfPlugins();
        MavenPluginBuilder plugin = MavenPluginBuilder.create()
                .setDependency(
                        DependencyBuilder.create()
                                .setGroupId("org.apache.maven.plugins")
                                .setArtifactId("maven-site-plugin")
                                .setVersion("3.0")
                );

        mavenPluginFacet.addPlugin(plugin);

        assertThat(getNumberOfPlugins(), is(nrOfPlugins + 1));
    }

    private int getNumberOfPlugins() {
        MavenCoreFacet mavenCoreFacet = getProject().getFacet(MavenCoreFacet.class);
        return mavenCoreFacet.getPOM().getBuild().getPlugins().size();
    }

    @Test
    public void testHasPlugin() {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        boolean hasPlugin = mavenPluginFacet.hasPlugin(DependencyBuilder.create("org.codehaus.mojo:findbugs-maven-plugin"));
        assertTrue(hasPlugin);
    }

    @Test
    public void testHasPluginForDefaultGroupId() {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        boolean hasPlugin = mavenPluginFacet.hasPlugin(DependencyBuilder.create("org.apache.maven.plugins:maven-compiler-plugin"));
        assertTrue(hasPlugin);
    }


    @Test
    public void testHasPluginForNullGroupId() {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        DependencyBuilder pluginDependency = DependencyBuilder.create().setArtifactId("maven-compiler-plugin");
        boolean hasPlugin = mavenPluginFacet.hasPlugin(pluginDependency);
        assertTrue(hasPlugin);
    }

    @Test
    public void testHasPluginForEmptyGroupId() {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        DependencyBuilder pluginDependency = DependencyBuilder.create()
                .setGroupId("")
                .setArtifactId("maven-compiler-plugin");
        boolean hasPlugin = mavenPluginFacet.hasPlugin(pluginDependency);
        assertTrue(hasPlugin);
    }


    @Test
    public void testHasPluginWhenPluginNotInstalled() {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        boolean hasPlugin = mavenPluginFacet.hasPlugin(DependencyBuilder.create("test.plugins:fake"));
        assertFalse(hasPlugin);
    }

    @Test
    public void testGetPlugin() {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        MavenPlugin plugin = mavenPluginFacet.getPlugin(DependencyBuilder.create("org.codehaus.mojo:findbugs-maven-plugin"));
        assertNotNull(plugin);
        assertThat(plugin.getDependency().getArtifactId(), is("findbugs-maven-plugin"));
        assertThat(plugin.getDependency().getVersion(), is("2.3.2"));
    }

    @Test(expected = PluginNotFoundException.class)
    public void testGetPluginException() {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        mavenPluginFacet.getPlugin(DependencyBuilder.create("test.plugins:fake"));
    }

    @Test
    public void testRemovePlugin() {
        MavenPluginFacet mavenPluginFacet = getProject().getFacet(MavenPluginFacet.class);
        int nrOfPlugins = mavenPluginFacet.listConfiguredPlugins().size();
        mavenPluginFacet.removePlugin(DependencyBuilder.create("org.apache.maven.plugins:maven-compiler-plugin"));
        assertThat(mavenPluginFacet.listConfiguredPlugins().size(), is(nrOfPlugins - 1));
    }

    @Test
    public void testAddConfigurationToExistingPlugin() {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        MavenPlugin plugin = mavenPluginFacet.getPlugin(DependencyBuilder.create("org.codehaus.mojo:findbugs-maven-plugin"));
        MavenPluginBuilder pluginBuilder = MavenPluginBuilder.create(plugin);

        pluginBuilder.createConfiguration()
                .createConfigurationElement("xmlOutput").setText("true");

        assertEquals("<plugin><groupId>org.codehaus.mojo</groupId><artifactId>findbugs-maven-plugin</artifactId><version>2.3.2</version><configuration><xmlOutput>true</xmlOutput></configuration></plugin>", pluginBuilder.toString());
    }

    @Test
    public void testAddConfigurationToExistingPluginWithConfig() {
        MavenPluginFacet mavenPluginFacet = testProject.getFacet(MavenPluginFacet.class);
        MavenPlugin plugin = mavenPluginFacet.getPlugin(DependencyBuilder.create().setArtifactId("maven-compiler-plugin"));
        MavenPluginBuilder pluginBuilder = MavenPluginBuilder.create(plugin);

        pluginBuilder.createConfiguration()
                .createConfigurationElement("testelement").setText("test");

        assertEquals("<plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId><version>2.0.2</version><configuration><source>1.6</source><target>1.6</target><testelement>test</testelement></configuration></plugin>", pluginBuilder.toString());
    }
}



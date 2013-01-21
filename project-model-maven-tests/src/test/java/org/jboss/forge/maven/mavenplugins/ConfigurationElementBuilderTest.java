/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.mavenplugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.junit.Test;

/**
 * @author <a href="mailto:paul.bakker.nl@gmail.com">Paul Bakker</a>
 */
public class ConfigurationElementBuilderTest
{
    private static final String XML = "<additionalClasspathElements><additionalClasspathElement>test</additionalClasspathElement></additionalClasspathElements>";
    private static final String XML_WITH_SUB_PLUGIN = "<reportPlugins><plugin><groupId>org.codehaus.mojo</groupId><artifactId>findbugs-maven-plugin</artifactId><version>2.3.2</version></plugin></reportPlugins>";
    private static final String XML_WITH_SUB_PLUGIN_AND_CONFIGURATION = "<reportPlugins><plugin><groupId>org.codehaus.mojo</groupId><artifactId>findbugs-maven-plugin</artifactId><version>2.3.2</version><configuration><xmlOutput>true</xmlOutput></configuration></plugin></reportPlugins>";

    private static final String COMPILER_PLUGIN = "<plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId><configuration><source>1.6</source><target>1.6</target></configuration></plugin>";
    private static final String SITE_PLUGIN = "<plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-site-plugin</artifactId><version>3.0</version><configuration><reportPlugins><plugin><groupId>org.codehaus.mojo</groupId><artifactId>findbugs-maven-plugin</artifactId><version>2.3.2</version><configuration><xmlOutput>true</xmlOutput></configuration></plugin></reportPlugins></configuration></plugin>";
    private static final String EAR_PLUGIN = "<plugin><artifactId>maven-ear-plugin</artifactId><version>2.5</version><configuration><modules><webModule><groupId>mygroupid</groupId><artifactId>myartifact</artifactId><contextRoot>/myapp</contextRoot></webModule></modules></configuration></plugin>";

    @Test
    public void testCreateConfigElement() {
        ConfigurationElementBuilder builder = ConfigurationElementBuilder.create()
                .setName("additionalClasspathElements")
                .addChild("additionalClasspathElement").setText("test").getParentElement();

        assertEquals(XML, builder.toString());

    }

    @Test
    public void testCreateWithSubPlugin() {
        MavenPluginBuilder findbugsPlugin = MavenPluginBuilder.create()
                .setDependency(
                        DependencyBuilder.create()
                                .setGroupId("org.codehaus.mojo")
                                .setArtifactId("findbugs-maven-plugin")
                                .setVersion("2.3.2")
                );

        ConfigurationElementBuilder builder = ConfigurationElementBuilder.create()
                .setName("reportPlugins")
                .addChild(findbugsPlugin);

        assertEquals(XML_WITH_SUB_PLUGIN, builder.toString());

    }

    @Test
    public void testCreateWithSubPluginWithConfiguration() {
        MavenPluginBuilder findbugsPlugin = MavenPluginBuilder.create()
                .setDependency(
                        DependencyBuilder.create()
                                .setGroupId("org.codehaus.mojo")
                                .setArtifactId("findbugs-maven-plugin")
                                .setVersion("2.3.2")
                )
                .createConfiguration()
                .createConfigurationElement("xmlOutput").setText("true").getParentPluginConfig()
                .getOrigin();


        ConfigurationElementBuilder builder = ConfigurationElementBuilder.create()
                .setName("reportPlugins")
                .addChild(findbugsPlugin);

        assertEquals(XML_WITH_SUB_PLUGIN_AND_CONFIGURATION, builder.toString());

    }

    @Test
    public void testCreateCompilerPlugin() {
        /*
        OUTPUT: ----------------

           <plugin>
               <groupId>org.apache.maven.plugins</groupId>
               <artifactId>maven-compiler-plugin</artifactId>
               <configuration>
                   <source>1.6</source>
                   <target>1.6</target>
               </configuration>
           </plugin>

        ---------------------
        */

        MavenPluginBuilder compilerPlugin = MavenPluginBuilder.create()
                .setDependency(
                        DependencyBuilder.create()
                                .setGroupId("org.apache.maven.plugins")
                                .setArtifactId("maven-compiler-plugin")
                )
                .createConfiguration().createConfigurationElement("source").setText("1.6").getParentPluginConfig()
                .createConfigurationElement("target").setText("1.6").getParentPluginConfig().getOrigin();


        assertEquals(COMPILER_PLUGIN, compilerPlugin.toString());
    }

    @Test
    public void testCreateSitePlugin() {
        /*
         OUTPUT: ----------------

             <plugin>
               <groupId>org.apache.maven.plugins</groupId>
               <artifactId>maven-site-plugin</artifactId>
               <version>3.0</version>
               <configuration>
                   <reportPlugins>
                       <plugin>
                           <groupId>org.codehaus.mojo</groupId>
                           <artifactId>findbugs-maven-plugin</artifactId>
                           <version>2.3.2</version>
                           <configuration>
                               <xmlOutput>true</xmlOutput>
                           </configuration>
                       </plugin>
                   </reportPlugins>
               </configuration>
           </plugin>

           ---------------------
        */

        MavenPluginBuilder findbugsPlugin = MavenPluginBuilder.create()
                .setDependency(
                        DependencyBuilder.create()
                                .setGroupId("org.codehaus.mojo")
                                .setArtifactId("findbugs-maven-plugin")
                                .setVersion("2.3.2")
                )
                .createConfiguration()
                .createConfigurationElement("xmlOutput")
                .setText("true").getParentPluginConfig().getOrigin();

        MavenPlugin sitePlugin = MavenPluginBuilder.create()
                .setDependency(
                        DependencyBuilder.create()
                                .setGroupId("org.apache.maven.plugins")
                                .setArtifactId("maven-site-plugin")
                                .setVersion("3.0")
                )
                .createConfiguration().createConfigurationElement("reportPlugins").addChild(findbugsPlugin).getParentPluginConfig().getOrigin();


        assertEquals(SITE_PLUGIN, sitePlugin.toString());
    }


    @Test
    public void testCreateEarPlugin() {
        /* OUTPUT --------------

        <plugin>
           <artifactId>maven-ear-plugin</artifactId>
           <version>2.5</version>
           <configuration>
              <modules>
                    <webModule>
                       <groupId>mygroupid</groupId>
                       <artifactId>myartifact</artifactId>
                       <contextRoot>/myapp</contextRoot>
                    </webModule>
              </modules>
           </configuration>
        </plugin>

        -----------------------
        */


        MavenPluginBuilder earPlugin = MavenPluginBuilder.create()
                .setDependency(
                        DependencyBuilder.create()
                                .setArtifactId("maven-ear-plugin")
                                .setVersion("2.5")
                )
                .createConfiguration()
                .createConfigurationElement("modules")
                .createConfigurationElement("webModule")
                .createConfigurationElement("groupId").setText("mygroupid").getParentElement()
                .createConfigurationElement("artifactId").setText("myartifact").getParentElement()
                .createConfigurationElement("contextRoot").setText("/myapp").getParentElement().getParentElement().getParentPluginConfig().getOrigin();

        assertEquals(EAR_PLUGIN, earPlugin.toString());
    }

    @Test
    public void testCreatePluginWithExensions() {
        MavenPluginBuilder plugin = MavenPluginBuilder.create().setDependency(
                DependencyBuilder.create()
                        .setGroupId("org.apache.maven.plugins")
                        .setArtifactId("maven-compiler-plugin")
        ).setExtensions(true);

        assertThat(plugin.toString(), containsString("<extensions>true</extensions>"));
    }
}

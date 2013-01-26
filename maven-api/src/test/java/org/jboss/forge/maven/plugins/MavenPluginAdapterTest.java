package org.jboss.forge.maven.plugins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.junit.Test;

public class MavenPluginAdapterTest
{

   @Test
   public void should_transform_simple_plugin()
   {
      // given
      Dependency compiler = mavenCompilerPlugin();
      MavenPluginBuilder builder = MavenPluginBuilder.create()
               .setDependency(compiler);
      
      // when
      Plugin plugin = new MavenPluginAdapter(builder);
      
      // then
      assertEquals(compiler.getGroupId(), plugin.getGroupId());
      assertEquals(compiler.getArtifactId(), plugin.getArtifactId());
      assertEquals(compiler.getVersion(), plugin.getVersion());
      assertNull(plugin.getExtensions());
   }
   
   @Test
   public void should_transform_plugin_with_configuration()
   {
      // given
      MavenPluginBuilder builder = MavenPluginBuilder.create()
               .setDependency(mavenCompilerPlugin())
               .setConfiguration(
                        ConfigurationBuilder.create()
                                 .addConfigurationElement(ConfigurationElementBuilder.create()
                                          .addChild("test").setText("content")));
      
      // when
      Plugin plugin = new MavenPluginAdapter(builder);
      
      // then
      assertNotNull(plugin.getConfiguration());
      assertNotNull(((Xpp3Dom) plugin.getConfiguration()).getChild("test"));
   }
   
   @Test
   public void should_transform_plugin_with_extension()
   {
      // given
      MavenPluginBuilder builder = MavenPluginBuilder.create()
               .setDependency(mavenCompilerPlugin())
               .setExtensions(true);
      // when
      Plugin plugin = new MavenPluginAdapter(builder);
      
      // then
      assertNotNull(plugin.getExtensions());
   }
   
   @Test
   public void should_transform_plugin_with_dependencies()
   {
      // given
      MavenPluginBuilder builder = MavenPluginBuilder.create()
               .setDependency(mavenCompilerPlugin())
               .addPluginDependency(groovyCompiler());
      
      // when
      Plugin plugin = new MavenPluginAdapter(builder);
      
      // then
      List<org.apache.maven.model.Dependency> dependencies = plugin.getDependencies();
      assertNotNull(dependencies);
      assertEquals(1, dependencies.size());
      org.apache.maven.model.Dependency dependency = dependencies.get(0);
      assertEquals("groovy-eclipse-compiler", dependency.getArtifactId());
      assertEquals("org.codehaus.groovy", dependency.getGroupId());
      assertEquals("2.7.0-01", dependency.getVersion());
      List<Exclusion> exclusions = dependency.getExclusions();
      assertFalse(exclusions.isEmpty());
      assertEquals("groovy-all", exclusions.get(0).getArtifactId());
   }
   
   @Test
   public void should_transform_plugin_with_executions()
   {
      // given
      MavenPluginBuilder builder = MavenPluginBuilder.create()
               .setDependency(mavenCompilerPlugin())
               .addExecution(ExecutionBuilder.create()
                        .addGoal("test")
                        .setId("testId")
                        .setPhase("test"));
      
      // when
      Plugin plugin = new MavenPluginAdapter(builder);
      
      // then
      assertFalse(plugin.getExecutions().isEmpty());
      PluginExecution execution = plugin.getExecutions().get(0);
      assertEquals("testId", execution.getId());
      assertFalse(execution.getGoals().isEmpty());
   }
   
   private Dependency groovyCompiler()
   {
      DependencyBuilder builder = DependencyBuilder.create(
            "org.codehaus.groovy:groovy-eclipse-compiler:2.7.0-01");
      builder.addExclusion()
            .setArtifactId("groovy-all")
            .setGroupId("org.codehaus.groovy");
      return builder;
   }

   private Dependency mavenCompilerPlugin()
   {
      return DependencyBuilder.create(
               "org.apache.maven.plugins:maven-compiler-plugin");
   }
}

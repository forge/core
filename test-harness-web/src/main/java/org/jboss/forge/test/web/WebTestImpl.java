/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.test.web;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ConfigurationElementBuilder;
import org.jboss.forge.maven.profiles.ProfileBuilder;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class WebTestImpl implements WebTest
{
   @Override
   public void setup(final Project project)
   {
      ResourceFacet resources = project.getFacet(ResourceFacet.class);
      FileResource<?> arquillian = resources.getTestResource("arquillian.xml");
      if (!arquillian.exists())
      {
         arquillian.createNewFile();
         arquillian.setContents(this.getClass().getResourceAsStream("/web/arquillian.xml"));
      }

      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      deps.addDirectManagedDependency(
               DependencyBuilder.create("org.jboss.arquillian:arquillian-bom:1.0.0.Final")
                        .setPackagingType(PackagingType.BASIC).setScopeType(ScopeType.IMPORT));

      ProfileBuilder profileBuilder = ProfileBuilder
               .create()
               .setId("JBOSS_AS_MANAGED_7_1")
               .setActiveByDefault(true)
               .addDependency(
                        DependencyBuilder.create("org.jboss.arquillian.junit:arquillian-junit-container"))
               .addDependency(
                        DependencyBuilder.create("org.jboss.arquillian.protocol:arquillian-protocol-servlet"))
               .addDependency(DependencyBuilder.create("org.jboss.jsfunit:jsfunit-arquillian:2.0.0.Beta2"))
               .addDependency(DependencyBuilder.create("junit:junit:4.10"))
               .addDependency(DependencyBuilder.create("org.jboss.shrinkwrap.descriptors:shrinkwrap-descriptors-impl:1.1.0-beta-1"))
               .addDependency(DependencyBuilder.create("org.jboss.as:jboss-as-arquillian-container-managed:7.1.1.Final"));

      Profile profile = profileBuilder.getAsMavenProfile();

      Build build = new Build();

      Plugin plugin = new Plugin();
      plugin.setArtifactId("maven-dependency-plugin");
      plugin.setExtensions(false);

      PluginExecution execution = new PluginExecution();
      execution.setId("unpack");
      execution.setPhase("process-test-classes");
      execution.addGoal("unpack");

      ConfigurationBuilder configBuilder = ConfigurationBuilder.create();
      ConfigurationElementBuilder artifactItem = configBuilder
               .createConfigurationElement("artifactItems").addChild("artifactItem");
      artifactItem.addChild("groupId").setText("org.jboss.as");
      artifactItem.addChild("artifactId").setText("jboss-as-dist");
      artifactItem.addChild("version").setText("7.1.1.Final");
      artifactItem.addChild("type").setText("zip");
      artifactItem.addChild("outputDirectory").setText("target/");
      try {
         new Xpp3DomBuilder();
         execution.setConfiguration(
                  Xpp3DomBuilder.build(new ByteArrayInputStream(configBuilder.toString().getBytes()), "UTF-8"));
      }
      catch (XmlPullParserException e) {
         throw new RuntimeException(e);
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }

      plugin.addExecution(execution);

      build.addPlugin(plugin);
      profile.setBuild(build);
      Model pom = mvn.getPOM();
      pom.addProfile(profile);
      mvn.setPOM(pom);
   }

   @Override
   public JavaClass from(final Project project, final Class<?> clazz)
   {
      try {
         return (JavaClass) project.getFacet(JavaSourceFacet.class).getTestJavaResource(clazz.getName())
                  .getJavaSource();
      }
      catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void addAsTestClass(final Project project, final JavaClass clazz)
   {
      try {
         JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
         clazz.setName(clazz.getName() + "Test").setPackage(java.getBasePackage());

         if (!clazz.hasAnnotation(RunWith.class))
         {
            Annotation<JavaClass> runWith = clazz.addAnnotation(RunWith.class);
            runWith.setLiteralValue("Arquillian.class");
         }

         if (clazz.hasAnnotation(Ignore.class))
         {
            clazz.removeAnnotation(clazz.getAnnotation(Ignore.class));
         }

         clazz.addImport(Arquillian.class);
         java.saveTestJavaSource(clazz);
      }
      catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Method<JavaClass> buildDefaultDeploymentMethod(final Project project, final JavaClass clazz,
            final Collection<String> deploymentItems)
   {
      try {
         JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

         JavaResource root = java.getTestJavaResource(java.getBasePackage() + ".Root");
         if (!root.exists())
         {
            java.saveTestJavaSource(JavaParser.create(JavaInterface.class).setName("Root")
                     .setPackage(java.getBasePackage()));
         }
         clazz.addImport(root.getJavaSource());

         clazz.addImport(WebArchive.class);
         clazz.addImport(Deployment.class);
         clazz.addImport(ShrinkWrap.class);

         Method<JavaClass> method = clazz.getMethod("getDeployment");

         if (method == null)
            method = clazz.addMethod("public static WebArchive getDeployment() {}");

         if (!method.hasAnnotation(Deployment.class))
            method.addAnnotation(Deployment.class);

         clazz.addImport(ExplodedImporter.class);
         clazz.addImport(JavaArchive.class);
         clazz.addImport(Filters.class);
         String body = "return ShrinkWrap.create(WebArchive.class)"
                  + ".addPackages(true, " + "Root.class.getPackage()" + ")";

         for (String item : deploymentItems)
         {
            body = body + item;
         }

         body = body + ".merge(ShrinkWrap.create(ExplodedImporter.class, \"temp.jar\")" +
                  ".importDirectory(\"src/main/webapp\") " +
                  ".as(JavaArchive.class),\"/\", Filters.includeAll());";

         method.setBody(body);

         return method;
      }
      catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      }
   }
}

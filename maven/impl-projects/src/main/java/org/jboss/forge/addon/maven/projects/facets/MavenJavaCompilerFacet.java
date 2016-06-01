/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.furnace.util.Assert;

/**
 * Configures the maven-compiler-plugin
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(MavenFacet.class)
public class MavenJavaCompilerFacet extends AbstractFacet<Project>implements JavaCompilerFacet
{

   static final String MAVEN_COMPILER_SOURCE_KEY = "maven.compiler.source";
   static final String MAVEN_COMPILER_TARGET_KEY = "maven.compiler.target";
   static final String MAVEN_COMPILER_ENCODING_KEY = "project.build.sourceEncoding";

   @Override
   public boolean isInstalled()
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      Properties properties = pom.getProperties();
      List<String> keys = Arrays.asList(MAVEN_COMPILER_SOURCE_KEY, MAVEN_COMPILER_TARGET_KEY,
               MAVEN_COMPILER_ENCODING_KEY);
      return properties.keySet().containsAll(keys);
   }

   @Override
   public boolean install()
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      Properties properties = pom.getProperties();

      setMavenCompilerSource(properties, DEFAULT_COMPILER_VERSION.toString());
      setMavenCompilerTarget(properties, DEFAULT_COMPILER_VERSION.toString());
      properties.setProperty(MAVEN_COMPILER_ENCODING_KEY, "UTF-8");
      maven.setModel(pom);
      return true;
   }

   @Override
   public void setSourceCompilerVersion(CompilerVersion version)
   {
      Assert.notNull(version, "The source compiler version must not be null");
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      setMavenCompilerSource(pom.getProperties(), version.toString());
      maven.setModel(pom);
   }

   @Override
   public void setTargetCompilerVersion(CompilerVersion version)
   {
      Assert.notNull(version, "The target compiler version must not be null");
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      setMavenCompilerTarget(pom.getProperties(), version.toString());
      maven.setModel(pom);
   }

   @Override
   public CompilerVersion getSourceCompilerVersion()
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      String sourceVersion = pom.getProperties().getProperty(MAVEN_COMPILER_SOURCE_KEY);
      return sourceVersion != null ? CompilerVersion.getValue(sourceVersion) : DEFAULT_COMPILER_VERSION;
   }

   @Override
   public CompilerVersion getTargetCompilerVersion()
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getModel();
      String targetVersion = pom.getProperties().getProperty(MAVEN_COMPILER_TARGET_KEY);
      return targetVersion != null ? CompilerVersion.getValue(targetVersion) : DEFAULT_COMPILER_VERSION;
   }

   private Properties setMavenCompilerSource(Properties mavenProps, String sourceCompilerVersion)
   {
      mavenProps.setProperty(MAVEN_COMPILER_SOURCE_KEY, sourceCompilerVersion);
      return mavenProps;
   }

   private void setMavenCompilerTarget(Properties mavenProps, String targetCompilerVersion)
   {
      mavenProps.setProperty(MAVEN_COMPILER_TARGET_KEY, targetCompilerVersion);
   }

}

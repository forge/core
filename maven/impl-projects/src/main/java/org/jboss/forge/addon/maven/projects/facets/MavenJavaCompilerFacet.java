/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Model;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.projects.Project;

/**
 * Configures the maven-compiler-plugin
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Dependent
@FacetConstraint(MavenFacet.class)
public class MavenJavaCompilerFacet extends AbstractFacet<Project> implements JavaCompilerFacet
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
      // TODO: Use System.getProperty("java.version") ?
      String javaVersion = "1.7";
      properties.setProperty(MAVEN_COMPILER_SOURCE_KEY, javaVersion);
      properties.setProperty(MAVEN_COMPILER_TARGET_KEY, javaVersion);
      properties.setProperty(MAVEN_COMPILER_ENCODING_KEY, "UTF-8");
      maven.setModel(pom);
      return true;
   }
}

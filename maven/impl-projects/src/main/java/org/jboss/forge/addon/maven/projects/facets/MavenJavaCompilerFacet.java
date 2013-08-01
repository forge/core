/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.furnace.exception.ContainerException;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
public class MavenJavaCompilerFacet extends AbstractFacet<Project> implements JavaCompilerFacet
{
   @Override
   public boolean isInstalled()
   {
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      Build build = pom.getBuild();
      if (build == null)
      {
         build = new Build();
      }
      List<Plugin> plugins = build.getPlugins();
      Plugin javaCompilerPlugin = getJavaCompilerPlugin(plugins);
      return javaCompilerPlugin != null;
   }

   @Override
   public boolean install()
   {
      // FIXME WOW this needs to be simplified somehow...
      MavenFacet maven = getFaceted().getFacet(MavenFacet.class);
      Model pom = maven.getPOM();
      Build build = pom.getBuild();
      if (build == null)
      {
         build = new Build();
      }
      List<Plugin> plugins = build.getPlugins();
      Plugin javaCompilerPlugin = getJavaCompilerPlugin(plugins);

      if (javaCompilerPlugin == null)
      {
         javaCompilerPlugin = new Plugin();
         // FIXME this should find the most recent version using DependencyResolver
         javaCompilerPlugin.setGroupId("org.apache.maven.plugins");
         javaCompilerPlugin.setArtifactId("maven-compiler-plugin");
         javaCompilerPlugin.setVersion("3.1");

         try
         {
            Xpp3Dom dom = Xpp3DomBuilder.build(
                     new ByteArrayInputStream(
                              ("<configuration>" +
                                       "<source>1.6</source>" +
                                       "<target>1.6</target>" +
                                       "<encoding>UTF-8</encoding>" +
                                       "</configuration>").getBytes()),
                     "UTF-8");

            javaCompilerPlugin.setConfiguration(dom);
         }
         catch (Exception e)
         {
            throw new ContainerException(e);
         }
      }

      build.addPlugin(javaCompilerPlugin);
      pom.setBuild(build);
      maven.setPOM(pom);
      return true;
   }

   private Plugin getJavaCompilerPlugin(List<Plugin> plugins)
   {
      Plugin javaSourcePlugin = null;
      for (Plugin plugin : plugins)
      {
         if ("org.apache.maven.plugins".equals(plugin.getGroupId())
                  && "maven-compiler-plugin".equals(plugin.getArtifactId()))
         {
            javaSourcePlugin = plugin;
         }
      }
      return javaSourcePlugin;
   }
}

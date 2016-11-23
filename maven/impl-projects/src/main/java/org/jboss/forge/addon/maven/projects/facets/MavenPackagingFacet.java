/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.facets;

import java.io.File;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.maven.projects.MavenFacet;
import org.jboss.forge.addon.maven.projects.MavenFacetImpl;
import org.jboss.forge.addon.maven.projects.MavenProjectBuilder;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.building.BuildResult;
import org.jboss.forge.addon.projects.building.ProjectBuilder;
import org.jboss.forge.addon.projects.events.PackagingChanged;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Strings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(MavenFacet.class)
public class MavenPackagingFacet extends AbstractFacet<Project> implements PackagingFacet
{
   @Override
   public void setPackagingType(final String type)
   {
      String oldType = getPackagingType();

      if (!oldType.equals(type))
      {
         MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
         Model pom = mavenFacet.getModel();
         pom.setPackaging(type);
         mavenFacet.setModel(pom);
         Addon addon = SimpleContainer.getAddon(getClass().getClassLoader());
         addon.getEventManager().fireEvent(new PackagingChanged(getFaceted(), oldType, type));
      }
   }

   @Override
   public String getPackagingType()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Model pom = mavenFacet.getModel();
      String packaging = pom.getPackaging();
      if (packaging != null)
      {
         packaging = packaging.trim();
      }
      return packaging;
   }

   @Override
   public boolean isInstalled()
   {
      return true;
   }

   @Override
   public boolean install()
   {
      if (getPackagingType() == null || getPackagingType().isEmpty())
      {
         setPackagingType("pom");
      }
      return true;
   }

   @Override
   public Resource<?> getFinalArtifact()
   {
      MavenFacet mvn = getFaceted().getFacet(MavenFacet.class);

      try
      {
         Build build = mvn.getEffectiveModel().getBuild();
         String directory = build.getDirectory();
         String finalName = build.getFinalName();

         if (Strings.isNullOrEmpty(directory))
         {
            throw new IllegalStateException("Project build directory is not configured");
         }
         if (Strings.isNullOrEmpty(finalName))
         {
            throw new IllegalStateException("Project final artifact name is not configured");
         }
         ResourceFactory resourceFactory = SimpleContainer
                  .getServices(getClass().getClassLoader(), ResourceFactory.class).get();
         return resourceFactory.create(new File(directory.trim(), finalName + "."
                  + getPackagingType().toLowerCase()));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not resolve build directory for project ["
                  + mvn.getModelResource().getFullyQualifiedName() + "]");
      }
   }

   @Override
   public Resource<?> executeBuild(final String... args)
   {
      return createBuilder().addArguments(args).build();
   }

   @Override
   public ProjectBuilder createBuilder()
   {
      Environment environment = SimpleContainer.getServices(getClass().getClassLoader(), Environment.class).get();
      return new MavenProjectBuilder(environment, getFaceted());
   }

   @Override
   public String getFinalName()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Model pom = mavenFacet.getModel();
      Build build = pom.getBuild();
      return build != null ? build.getFinalName() : getDefaultFinalName();
   }

   /**
    * @return The maven calculated final name as specified in http://maven.apache.org/pom.html
    */
   private String getDefaultFinalName()
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Model pom = mavenFacet.getModel();
      String version = pom.getVersion();
      if (version == null && pom.getParent() != null)
         version = pom.getParent().getVersion();
      return pom.getArtifactId() + "-" + version;
   }

   @Override
   public void setFinalName(final String finalName)
   {
      MavenFacet mavenFacet = getFaceted().getFacet(MavenFacet.class);
      Model pom = mavenFacet.getModel();
      Build build = pom.getBuild();
      if (build == null)
      {
         build = new Build();
         pom.setBuild(build);
      }
      pom.getBuild().setFinalName(finalName);
      mavenFacet.setModel(pom);
   }

   @Override
   public BuildResult getBuildResult()
   {
      MavenFacetImpl mvn = getFaceted().getFacet(MavenFacetImpl.class);
      return mvn.getEffectiveModelBuildResult();
   }
}

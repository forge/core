/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.facets;

import java.io.File;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.build.ProjectBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.packaging.events.PackagingChanged;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Dependent
@Alias("forge.maven.PackagingFacet")
@RequiresFacet(MavenCoreFacet.class)
public class MavenPackagingFacet extends BaseFacet implements PackagingFacet, Facet
{
   @Inject
   private Event<PackagingChanged> event;

   @Inject
   private ResourceFactory factory;

   @Inject
   private Shell shell;

   @Inject
   private ForgeEnvironment environment;

   @Override
   public void setPackagingType(final PackagingType type)
   {
      PackagingType oldType = getPackagingType();

      if (!oldType.equals(type))
      {
         MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
         Model pom = mavenFacet.getPOM();
         pom.setPackaging(type.getType());
         mavenFacet.setPOM(pom);

         event.fire(new PackagingChanged(project, oldType, type));
      }
   }

   @Override
   public PackagingType getPackagingType()
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenFacet.getPOM();
      return PackagingType.from(pom.getPackaging());
   }

   @Override
   public boolean isInstalled()
   {
      try
      {
         project.getFacet(MavenCoreFacet.class);
         return true;
      }
      catch (FacetNotFoundException e)
      {
         return false;
      }
   }

   @Override
   public boolean install()
   {
      if (PackagingType.NONE.equals(getPackagingType()))
      {
         setPackagingType(PackagingType.BASIC);
      }
      return true;
   }

   @Override
   public Resource<?> getFinalArtifact()
   {
      MavenCoreFacet mvn = project.getFacet(MavenCoreFacet.class);
      String directory = mvn.getPartialProjectBuildingResult().getProject().getBuild().getDirectory();
      String finalName = mvn.getPartialProjectBuildingResult().getProject().getBuild().getFinalName();

      if (Strings.isNullOrEmpty(directory))
      {
         throw new IllegalStateException("Project build directory is not configured");
      }
      if (Strings.isNullOrEmpty(finalName))
      {
         throw new IllegalStateException("Project final artifact name is not configured");
      }
      return factory.getResourceFrom(new File(directory.trim() + "/" + finalName + "."
               + getPackagingType().getType().toLowerCase()));
   }

   @Override
   public Resource<?> executeBuild(final String... args)
   {
      return createBuilder().addArguments(args).build();
   }

   @Override
   public ProjectBuilder createBuilder()
   {
      return new MavenProjectBuilder(environment, project);
   }

   @Override
   public String getFinalName()
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenFacet.getPOM();
      Build build = pom.getBuild();
      return build != null ? build.getFinalName() : getDefaultFinalName();
   }

   /**
    * @return The maven calculated final name as specified in http://maven.apache.org/pom.html
    */
   private String getDefaultFinalName()
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenFacet.getPOM();
      String version = pom.getVersion();
      if (version == null && pom.getParent() != null)
         version = pom.getParent().getVersion();
      return pom.getArtifactId() + "-" + version;
   }

   @Override
   public void setFinalName(final String finalName)
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenFacet.getPOM();
      Build build = pom.getBuild();
      if (build == null)
      {
         build = new Build();
         pom.setBuild(build);
      }
      pom.getBuild().setFinalName(finalName);
      mavenFacet.setPOM(pom);
   }
}

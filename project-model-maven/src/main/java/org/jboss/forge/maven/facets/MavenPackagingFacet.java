/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.maven.facets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.project.packaging.events.PackagingChanged;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.shrinkwrap.descriptor.impl.base.Strings;

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
      String directory = mvn.getProjectBuildingResult().getProject().getBuild().getDirectory();
      String finalName = mvn.getProjectBuildingResult().getProject().getBuild().getFinalName();

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
      MavenCli cli = new MavenCli();
      String[] defaults = new String[] { "clean", "package" };
      String[] selected = defaults;
      if ((args != null) && (args.length > 0))
      {
         selected = args;
      }

      if (!environment.isOnline())
      {
         List<String> list = new ArrayList<String>(Arrays.asList(selected));
         list.add("--offline");
         selected = list.toArray(new String[list.size()]);
      }

      int i = cli.doMain(selected, project.getProjectRoot().getFullyQualifiedName(),
               System.out, System.err);

      if (i == 0)
      {
         ShellMessages.success(shell, "Build successful.");
         return getFinalArtifact();
      }
      else
      {
         ShellMessages.error(shell, "Build failed.");
         return null;
      }
   }

   @Override
   public String getFinalName()
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenFacet.getPOM();
      return pom.getBuild().getFinalName();
   }

   @Override
   public void setFinalName(final String finalName)
   {
      MavenCoreFacet mavenFacet = project.getFacet(MavenCoreFacet.class);
      Model pom = mavenFacet.getPOM();
      pom.getBuild().setFinalName(finalName);
      mavenFacet.setPOM(pom);
   }
}

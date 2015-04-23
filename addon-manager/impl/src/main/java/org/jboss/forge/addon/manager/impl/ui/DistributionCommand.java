/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.impl.ui;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependencies.util.CompositeDependencyFilter;
import org.jboss.forge.addon.dependencies.util.NonSnapshotDependencyFilter;
import org.jboss.forge.addon.manager.impl.utils.DistributionDirectoryExistsPredicate;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.predicate.NonGUIEnabledPredicate;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Predicate;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.furnace.versions.Versions;

/**
 * Contains commands for updating the Forge distribution
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class DistributionCommand
{
   @Inject
   private DependencyResolver resolver;

   @Inject
   private ResourceFactory resourceFactory;

   @Command(value = "Forge: Update Abort", help = "Aborts a previous forge update", categories = { "Forge", "Manage" }, enabled = {
            NonGUIEnabledPredicate.class, DistributionDirectoryExistsPredicate.class })
   public Result updateAbort() throws IOException
   {
      DirectoryResource forgeHome = getForgeHome();
      DirectoryResource updateDirectory = forgeHome.getChildDirectory(".update");
      if (updateDirectory.exists())
      {
         if (updateDirectory.delete(true))
         {
            return Results
                     .success("Update files were deleted. Run 'forge-update' if you want to update this installation again.");
         }
         else
         {
            return Results.fail("Could not abort. Try to run 'forge-update-abort' again");
         }
      }
      else
      {
         return Results.success("No update files found");
      }
   }

   @Command(value = "Forge: Update", help = "Update this forge installation", categories = { "Forge", "Manage" }, enabled = NonGUIEnabledPredicate.class)
   public void update(UIOutput output, UIPrompt prompt, UIProgressMonitor monitor) throws IOException
   {
      PrintStream out = output.out();
      DirectoryResource forgeHome = getForgeHome();
      DirectoryResource updateDir = forgeHome.getChildDirectory(".update");
      if (updateDir.exists())
      {
         output.warn(
                  out,
                  "There is an update pending. Restart Forge for the update to take effect. To abort this update, type 'forge-update-abort'");
         return;
      }
      Coordinate forgeDistribution = getLatestAvailableDistribution();
      if (forgeDistribution == null)
      {
         output.info(out, "Forge is up to date! Enjoy!");
      }
      else
      {
         output.info(out, "This Forge installation will be updated to " + forgeDistribution.getVersion());
         if (prompt.promptBoolean("Is that ok ?", true))
         {
            updateForge(forgeHome, forgeDistribution, output);
         }
      }
   }

   private DirectoryResource getForgeHome()
   {
      DirectoryResource forgeHome = resourceFactory.create(DirectoryResource.class,
               OperatingSystemUtils.getForgeHomeDir());
      return forgeHome;
   }

   /**
    * @param forgeDistribution
    */
   private void updateForge(final DirectoryResource forgeHome, final Coordinate forgeDistribution, UIOutput output)
            throws IOException
   {
      // wait.start("Update in progress. Please wait");
      Dependency dependency = resolver.resolveArtifact(DependencyQueryBuilder.create(forgeDistribution));
      Assert.notNull(dependency, "Artifact was not found");
      resourceFactory.create(dependency.getArtifact().getUnderlyingResourceObject());
      // Files.unzip(resource.getUnderlyingResourceObject(), forgeHome.getUnderlyingResourceObject());

      DirectoryResource childDirectory = forgeHome.getChildDirectory(dependency.getCoordinate().getArtifactId() + "-"
               + dependency.getCoordinate().getVersion());

      DirectoryResource updateDirectory = forgeHome.getChildDirectory(".update");
      if (updateDirectory.exists())
      {
         updateDirectory.delete(true);
      }
      childDirectory.renameTo(updateDirectory);
      // wait.stop();
      output.success(output.out(), "Forge will now restart to complete the update...");
      System.exit(0);
   }

   /**
    * Returns the latest available distribution
    */
   private Coordinate getLatestAvailableDistribution()
   {
      final Version runtimeVersion = Versions.getImplementationVersionFor(getClass());

      CoordinateBuilder distCoordinate = CoordinateBuilder.create().setGroupId("org.jboss.forge")
               .setArtifactId("forge-distribution")
               .setClassifier("offline").setPackaging("zip");

      DependencyQuery query = DependencyQueryBuilder
               .create(distCoordinate)
               .setFilter(new CompositeDependencyFilter(
                        new NonSnapshotDependencyFilter(),
                        new Predicate<Dependency>()
                        {
                           @Override
                           public boolean accept(Dependency dependency)
                           {
                              Version version = new SingleVersion(dependency.getCoordinate().getVersion());
                              return version.compareTo(runtimeVersion) > 0 && version.getMajorVersion() == 2
                                       && version.getQualifier().equals("Final");
                           }
                        }
                        ));
      List<Coordinate> versions = resolver.resolveVersions(query);
      return versions.isEmpty() ? null : versions.get(versions.size() - 1);
   }

}

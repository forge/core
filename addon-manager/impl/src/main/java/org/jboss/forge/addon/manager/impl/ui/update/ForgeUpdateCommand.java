/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.manager.impl.ui.update;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyQuery;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependencies.util.CompositeDependencyFilter;
import org.jboss.forge.addon.dependencies.util.NonSnapshotDependencyFilter;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.zip.ZipFileResource;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
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
public class ForgeUpdateCommand extends AbstractUICommand
{
   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // No inputs
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      DependencyResolver resolver = SimpleContainer.getServices(getClass().getClassLoader(), DependencyResolver.class)
               .get();
      UIProvider provider = context.getUIContext().getProvider();
      UIOutput output = provider.getOutput();
      UIPrompt prompt = context.getPrompt();
      PrintStream out = output.out();
      DirectoryResource forgeHome = getForgeHome();
      DirectoryResource updateDir = forgeHome.getChildDirectory(".update");
      if (updateDir.exists())
      {
         return Results.fail(
                  "There is an update pending. Restart Forge for the update to take effect. To abort this update, type 'forge-update-abort'");
      }
      Coordinate forgeDistribution = getLatestAvailableDistribution(resolver);
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
      return Results.success();
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      UIProvider provider = context.getProvider();
      return !provider.isEmbedded() && !provider.isGUI();
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Forge: Update").description("Update this forge installation")
               .category(Categories.create("Forge", "Manage"));
   }

   private DirectoryResource getForgeHome()
   {
      ResourceFactory resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class)
               .get();
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
      DependencyResolver resolver = SimpleContainer
               .getServices(getClass().getClassLoader(), DependencyResolver.class).get();
      ResourceFactory resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class)
               .get();
      Dependency dependency = resolver.resolveArtifact(DependencyQueryBuilder.create(forgeDistribution));
      Assert.notNull(dependency, "Artifact was not found");
      ZipFileResource dependencyZip = resourceFactory.create(ZipFileResource.class,
               dependency.getArtifact().getUnderlyingResourceObject());
      dependencyZip.extractTo(forgeHome);
      DirectoryResource childDirectory = forgeHome.getChildDirectory(dependency.getCoordinate().getArtifactId() + "-"
               + dependency.getCoordinate().getVersion());
      DirectoryResource updateDirectory = forgeHome.getChildDirectory(".update");
      childDirectory.renameTo(updateDirectory);
      output.success(output.out(), "Forge will now restart to complete the update...");
      System.exit(0);
   }

   /**
    * Returns the latest available distribution
    */
   private Coordinate getLatestAvailableDistribution(DependencyResolver resolver)
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
                              Version version = SingleVersion.valueOf(dependency.getCoordinate().getVersion());
                              return version.compareTo(runtimeVersion) > 0 
                                       && version.getMajorVersion() == runtimeVersion.getMajorVersion()
                                       && "Final".equals(version.getQualifier());
                           }
                        }));
      List<Coordinate> versions = resolver.resolveVersions(query);
      return versions.isEmpty() ? null : versions.get(versions.size() - 1);
   }

}

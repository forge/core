package org.jboss.forge.addon.manager.impl.ui;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.git.GitUtils;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.building.BuildException;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.URLResource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.manager.AddonManager;
import org.jboss.forge.furnace.manager.request.InstallRequest;
import org.jboss.forge.furnace.manager.request.RemoveRequest;
import org.jboss.forge.furnace.util.Addons;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * Installs the addon via Git (needs the git addon installed)
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonGitBuildAndInstallCommand extends AbstractUICommand implements AddonCommandConstants
{
   @Inject
   private AddonManager addonManager;

   @Inject
   @WithAttributes(shortName = 'u', label = "GIT Repository URL", description = "The git repository location", required = true)
   private UIInput<URLResource> url;

   @Inject
   @WithAttributes(shortName = 'c', label = "Coordinate", description = "The coordinates of this addon if multiple addons are available")
   private UIInput<String> coordinate;

   @Inject
   @WithAttributes(shortName = 'r', label = "Branch/Tag", description = "The branch/tag (ref) to use if different from default")
   private UIInput<String> ref;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private AddonRegistry registry;

   @Inject
   private GitUtils gitUtils;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private Furnace furnace;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      boolean gui = context.getProvider().isGUI();
      return Metadata
               .from(super.getMetadata(context), getClass())
               .name(gui ? ADDON_BUILD_INSTALL_COMMAND_NAME_FROM_GIT : ADDON_BUILD_INSTALL_COMMAND_NAME_FROM_GIT_NO_GUI)
               .description(ADDON_BUILD_INSTALL_COMMAND_DESCRIPTION)
               .category(Categories.create(ADDON_MANAGER_CATEGORIES));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(url).add(ref).add(coordinate);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      // TODO: Option to save sources?
      File tempDir = OperatingSystemUtils.createTempDir();
      DirectoryResource projectRoot = resourceFactory.create(DirectoryResource.class, tempDir);
      UIProgressMonitor progressMonitor = context.getProgressMonitor();
      UIOutput output = context.getUIContext().getProvider().getOutput();
      progressMonitor.beginTask("Installing Addon from Git", 4);

      progressMonitor.subTask("Cloning repository in " + tempDir);

      // Clone repository
      cloneTo(projectRoot);

      progressMonitor.worked(1);
      progressMonitor.subTask("Installing project into local repository");
      // Build project
      Project project = projectFactory.findProject(projectRoot);
      if (project == null)
      {
         return Results.fail("No project found in root " + projectRoot.getFullyQualifiedName());
      }
      Coordinate buildCoordinate = project.getFacet(MetadataFacet.class).getOutputDependency().getCoordinate();
      try
      {
         project.getFacet(PackagingFacet.class).createBuilder()
                  .addArguments("clean", "install", "-Dversion.furnace=" + furnace.getVersion())
                  .runTests(false)
                  .build(output.out(), output.err());
      }
      catch (BuildException e)
      {
         return Results.fail("Unable to execute project build", e);
      }
      progressMonitor.worked(1);
      AddonId id = null;
      try
      {
         if (coordinate.hasValue())
         {
            try
            {
               id = AddonId.fromCoordinates(coordinate.getValue());
            }
            catch (IllegalArgumentException e)
            {
               id = AddonId.from(coordinate.getValue(), buildCoordinate.getVersion());
            }
         }
         else
         {
            id = AddonId.from(buildCoordinate.getGroupId() + ":" + buildCoordinate.getArtifactId(),
                     buildCoordinate.getVersion());
         }
         progressMonitor.subTask("Removing previous addon installation (" + id + ")");
         RemoveRequest removeRequest = addonManager.remove(id);
         removeRequest.perform();
         Addons.waitUntilStopped(registry.getAddon(id));
         progressMonitor.worked(1);

         progressMonitor.subTask("Installing addon (" + id + ")");
         InstallRequest installRequest = addonManager.install(id);
         installRequest.perform();
         progressMonitor.done();
         return Results.success("Addon " + id + " was installed successfully.");
      }
      catch (Throwable t)
      {
         return Results.fail("Addon " + id + " could not be installed: " + t.getMessage(), t);
      }
   }

   private void cloneTo(DirectoryResource projectRoot) throws GitAPIException, IOException
   {
      Git git = null;
      try
      {
         git = gitUtils.clone(projectRoot, url.getValue().getFullyQualifiedName());
         if (ref.hasValue())
         {
            String refName = ref.getValue();
            String currentBranch = git.getRepository().getBranch();
            // No need to checkout if the branch name is the same
            if (!currentBranch.equals(refName))
            {
               git.checkout().
                        setCreateBranch(true).
                        setName(refName).
                        setUpstreamMode(SetupUpstreamMode.TRACK).
                        setStartPoint("origin/" + refName).
                        call();
            }
         }
      }
      finally
      {
         gitUtils.close(git);
      }
   }
}

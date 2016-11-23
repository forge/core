/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.project;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.addons.facets.AddonAPIFacet;
import org.jboss.forge.addon.addons.facets.AddonAddonFacet;
import org.jboss.forge.addon.addons.facets.AddonClassifierFacet;
import org.jboss.forge.addon.addons.facets.AddonImplFacet;
import org.jboss.forge.addon.addons.facets.AddonParentFacet;
import org.jboss.forge.addon.addons.facets.AddonSPIFacet;
import org.jboss.forge.addon.addons.facets.AddonTestFacet;
import org.jboss.forge.addon.addons.facets.DefaultFurnaceContainerFacet;
import org.jboss.forge.addon.addons.facets.ForgeBOMFacet;
import org.jboss.forge.addon.addons.facets.ForgeVersionFacet;
import org.jboss.forge.addon.addons.facets.FurnacePluginFacet;
import org.jboss.forge.addon.addons.facets.FurnaceVersionFacet;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.maven.projects.MavenBuildSystem;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaPackageInfoSource;

/**
 * Creates Furnace Addon projects
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("unchecked")
public class AddonProjectConfiguratorImpl implements AddonProjectConfigurator
{
   private static final String FORGE_ADDON_CLASSIFIER = "forge-addon";

   @Override
   public void setupSimpleAddonProject(Project project, Iterable<AddonId> dependencyAddons)
            throws FileNotFoundException, FacetNotFoundException
   {
      FacetFactory facetFactory = getFacetFactory();
      generateReadme(project);
      facetFactory.install(project, FurnaceVersionFacet.class);
      facetFactory.install(project, ForgeVersionFacet.class);
      facetFactory.install(project, ForgeBOMFacet.class);
      facetFactory.install(project, FurnacePluginFacet.class);
      facetFactory.install(project, AddonClassifierFacet.class);
      facetFactory.install(project, JavaSourceFacet.class);
      facetFactory.install(project, ResourcesFacet.class);
      facetFactory.install(project, JavaCompilerFacet.class);
      facetFactory.install(project, DefaultFurnaceContainerFacet.class);
      facetFactory.install(project, CDIFacet_1_1.class);
      facetFactory.install(project, AddonTestFacet.class);

      JavaSourceFacet javaSource = project.getFacet(JavaSourceFacet.class);
      javaSource.saveJavaSource(Roaster.create(JavaPackageInfoSource.class).setPackage(javaSource.getBasePackage()));

      installSelectedAddons(project, dependencyAddons, false);
   }

   /**
    * Create a Furnace Project with the full structure (api,impl,tests,spi and addon)
    * 
    * @throws FacetNotFoundException
    * @throws FileNotFoundException
    */
   @Override
   public void setupComplexAddonProject(Project project, Iterable<AddonId> dependencyAddons)
            throws FileNotFoundException, FacetNotFoundException
   {
      FacetFactory facetFactory = getFacetFactory();
      DependencyInstaller dependencyInstaller = getDependencyInstaller();
      generateReadme(project);
      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      String projectName = metadata.getProjectName();
      metadata.setProjectName(projectName + "-parent");

      project.getFacet(PackagingFacet.class).setPackagingType("pom");

      facetFactory.install(project, AddonParentFacet.class);
      facetFactory.install(project, ForgeBOMFacet.class);

      Project addonProject = createSubmoduleProject(project, "addon", projectName, AddonAddonFacet.class);
      Project apiProject = createSubmoduleProject(project, "api", projectName + "-api", AddonAPIFacet.class,
               CDIFacet_1_1.class);
      Project implProject = createSubmoduleProject(project, "impl", projectName + "-impl", AddonImplFacet.class,
               CDIFacet_1_1.class);
      Project spiProject = createSubmoduleProject(project, "spi", projectName + "-spi", AddonSPIFacet.class);
      Project testsProject = createSubmoduleProject(project, "tests", projectName + "-tests", AddonTestFacet.class);

      Dependency apiProjectDependency = apiProject.getFacet(MetadataFacet.class).getOutputDependency();
      Dependency implProjectDependency = implProject.getFacet(MetadataFacet.class).getOutputDependency();

      Dependency spiProjectDependency = DependencyBuilder.create(
               spiProject.getFacet(MetadataFacet.class).getOutputDependency())
               .setClassifier(FORGE_ADDON_CLASSIFIER);

      Dependency addonProjectDependency = DependencyBuilder.create(
               addonProject.getFacet(MetadataFacet.class).getOutputDependency())
               .setClassifier(FORGE_ADDON_CLASSIFIER);

      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(addonProjectDependency).setVersion("${project.version}"));
      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(apiProjectDependency).setVersion("${project.version}"));
      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(implProjectDependency).setVersion("${project.version}"));
      dependencyInstaller.installManaged(project,
               DependencyBuilder.create(spiProjectDependency).setVersion("${project.version}"));

      for (Project p : Arrays.asList(addonProject, apiProject, implProject, spiProject))
      {
         JavaSourceFacet javaSource = p.getFacet(JavaSourceFacet.class);
         javaSource.saveJavaSource(Roaster.create(JavaPackageInfoSource.class).setPackage(javaSource.getBasePackage()));
      }

      installSelectedAddons(project, dependencyAddons, true);
      installSelectedAddons(addonProject, dependencyAddons, false);
      installSelectedAddons(apiProject, dependencyAddons, false);
      installSelectedAddons(testsProject, dependencyAddons, false);

      dependencyInstaller.install(addonProject, DependencyBuilder.create(apiProjectDependency));
      dependencyInstaller.install(addonProject, DependencyBuilder.create(implProjectDependency)
               .setOptional(true)
               .setScopeType("runtime"));
      dependencyInstaller.install(addonProject, DependencyBuilder.create(spiProjectDependency));

      dependencyInstaller.install(implProject, DependencyBuilder.create(apiProjectDependency).setScopeType("provided"));
      dependencyInstaller.install(implProject, DependencyBuilder.create(spiProjectDependency).setScopeType("provided"));

      dependencyInstaller.install(apiProject, DependencyBuilder.create(spiProjectDependency).setScopeType("provided"));

      dependencyInstaller.install(testsProject, addonProjectDependency);
   }

   /**
    * @param project
    */
   private void generateReadme(Project project)
   {
      String readmeTemplate = Streams.toString(getClass().getResourceAsStream("README.asciidoc"));
      FileResource<?> child = project.getRoot().reify(DirectoryResource.class)
               .getChildOfType(FileResource.class, "README.asciidoc");

      // TODO: Replace with template addon
      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      readmeTemplate = readmeTemplate.replaceAll("\\{\\{ADDON_GROUP_ID\\}\\}", metadata.getProjectGroupName());
      readmeTemplate = readmeTemplate.replaceAll("\\{\\{ADDON_ARTIFACT_ID\\}\\}", metadata.getProjectName());
      child.createNewFile();
      child.setContents(readmeTemplate);
   }

   @Override
   public void installSelectedAddons(final Project project, Iterable<AddonId> addons, boolean managed)
   {
      DependencyInstaller dependencyInstaller = getDependencyInstaller();
      if (addons != null)
         for (AddonId addon : addons)
         {
            Dependency dependency = toDependency(addon);
            if (managed)
            {
               if (!dependencyInstaller.isManaged(project, dependency))
               {
                  dependencyInstaller.installManaged(project, dependency);
               }
            }
            else
            {
               if (!dependencyInstaller.isInstalled(project, dependency))
               {
                  dependencyInstaller.install(project, dependency);
               }
            }
         }
   }

   @Override
   public Dependency toDependency(AddonId addon)
   {
      String[] mavenCoords = addon.getName().split(":");
      Dependency dependency = DependencyBuilder.create().setGroupId(mavenCoords[0])
               .setArtifactId(mavenCoords[1])
               .setVersion(addon.getVersion().toString()).setClassifier(FORGE_ADDON_CLASSIFIER);
      return dependency;
   }

   /**
    * Checks if the {@link Project} depends on the provided {@link AddonId}
    */
   @Override
   public boolean dependsOnAddon(final Project project, AddonId addonId)
   {
      DependencyInstaller dependencyInstaller = getDependencyInstaller();
      Dependency dependency = toDependency(addonId);
      return dependencyInstaller.isInstalled(project, dependency);
   }

   private Project createSubmoduleProject(final Project parent, String moduleName, String artifactId,
            Class<? extends ProjectFacet>... requiredProjectFacets)
   {
      ProjectFactory projectFactory = getProjectFactory();
      MavenBuildSystem buildSystem = getBuildSystem();
      DirectoryResource location = parent.getRoot().reify(DirectoryResource.class)
               .getOrCreateChildDirectory(moduleName);

      List<Class<? extends ProjectFacet>> facets = new ArrayList<>();
      facets.add(ResourcesFacet.class);
      facets.addAll(Arrays.asList(requiredProjectFacets));
      Project project = projectFactory.createProject(location, buildSystem, facets);

      MetadataFacet metadata = project.getFacet(MetadataFacet.class);
      metadata.setProjectName(artifactId);
      return project;
   }

   /**
    * @return the facetFactory
    */
   private FacetFactory getFacetFactory()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
   }

   /**
    * @return the projectFactory
    */
   private ProjectFactory getProjectFactory()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class).get();
   }

   /**
    * @return the dependencyInstaller
    */
   private DependencyInstaller getDependencyInstaller()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), DependencyInstaller.class).get();
   }

   /**
    * @return the buildSystem
    */
   private MavenBuildSystem getBuildSystem()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), MavenBuildSystem.class).get();
   }
}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.stacks.NoStackFacet;
import org.jboss.forge.addon.projects.stacks.StackFacet;
import org.jboss.forge.addon.projects.stacks.StackFacetComparator;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Commands;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Sets;

public class NewProjectWizardImpl implements UIWizard, NewProjectWizard
{
   private static final Logger log = Logger.getLogger(NewProjectWizardImpl.class.getName());
   private static final Pattern VALID_PACKAGE_PATTERN = Pattern.compile("(?i)(~\\.)?([a-z0-9_]+\\.?)+[a-z0-9_]");

   private UIInput<String> named;
   private UIInput<String> topLevelPackage;
   private UIInput<String> version;
   private UIInput<String> finalName;
   private UIInput<DirectoryResource> targetLocation;
   private UIInput<Boolean> useTargetLocationRoot;
   private UIInput<Boolean> overwrite;
   private UISelectOne<ProjectType> type;
   private UISelectOne<ProjectProvider> buildSystem;
   private UISelectOne<StackFacet> stack;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Project: New").description("Create a new project")
               .category(Categories.create("Project", "Generation"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return !SimpleContainer.getServices(getClass().getClassLoader(), ProjectProvider.class).isUnsatisfied();
   }

   @Override
   public void initializeUI(final UIBuilder builder) throws Exception
   {
      UIContext uiContext = builder.getUIContext();
      InputComponentFactory factory = builder.getInputComponentFactory();
      configureProjectNamedInput(factory);
      configureTopLevelPackageInput(factory);
      configureVersionInput(factory);
      configureFinalName(factory);
      configureTargetLocationInput(factory, uiContext);
      configureUseTargetLocationRootInput(factory, uiContext);
      configureOverwriteInput(factory);
      configureBuildSystemInput(factory, uiContext);
      configureProjectTypeInput(factory, uiContext);
      configureStack(factory, uiContext);
      builder.add(named).add(topLevelPackage).add(version).add(finalName).add(targetLocation).add(useTargetLocationRoot)
               .add(overwrite).add(type)
               .add(buildSystem).add(stack);
   }

   private void configureFinalName(InputComponentFactory factory)
   {
      finalName = factory.createInput("finalName", String.class).setLabel("Final name");
   }

   private void configureProjectNamedInput(InputComponentFactory factory)
   {
      named = factory.createInput("named", String.class).setLabel("Project name").setRequired(true);
      named.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext context)
         {
            if (named.getValue() != null && named.getValue().matches(".*[^-_.a-zA-Z0-9].*"))
               context.addValidationError(named,
                        "Project name must not contain spaces or special characters.");
         }
      });
   }

   private void configureVersionInput(InputComponentFactory factory)
   {
      version = factory.createInput("version", String.class).setLabel("Version");
      version.setDefaultValue("1.0.0-SNAPSHOT");
   }

   private void configureTargetLocationInput(InputComponentFactory factory, final UIContext uiContext)
   {
      final ResourceFactory resourceFactory = SimpleContainer
               .getServices(getClass().getClassLoader(), ResourceFactory.class).get();
      targetLocation = factory.createInput("targetLocation", DirectoryResource.class).setLabel("Project location");
      final DirectoryResource defaultValue;
      UISelection<Resource<?>> currentSelection = uiContext.getInitialSelection();
      if (currentSelection.isEmpty())
      {
         defaultValue = resourceFactory.create(DirectoryResource.class, OperatingSystemUtils.getUserHomeDir());
      }
      else
      {
         Resource<?> resource = currentSelection.get();
         if (resource instanceof DirectoryResource)
         {
            defaultValue = (DirectoryResource) resource;
         }
         else
         {
            defaultValue = resourceFactory.create(DirectoryResource.class, OperatingSystemUtils.getUserHomeDir());
         }
      }
      targetLocation.setDefaultValue(defaultValue).setValueConverter(defaultValue::resolveAsDirectory);
   }

   private void configureUseTargetLocationRootInput(InputComponentFactory factory, final UIContext context)
   {
      useTargetLocationRoot = factory.createInput("useTargetLocationRoot", Boolean.class)
               .setLabel("Use Target Location Root?")
               .setDescription("If specified, it won't create a subdirectory inside the specified Project location");
   }

   private void configureOverwriteInput(InputComponentFactory factory)
   {
      overwrite = factory.createInput("overwrite", Boolean.class).setLabel("Overwrite existing project location");
      overwrite.setDefaultValue(false).setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            DirectoryResource targetDirectory = getTargetDirectory();
            // Enable Overwrite flag if target exists and it is not empty
            return named.hasValue() && targetDirectory.exists() && !targetDirectory.listResources().isEmpty();
         }
      });
   }

   private void configureProjectTypeInput(InputComponentFactory factory, final UIContext uiContext)
   {
      type = factory.createSelectOne("type", ProjectType.class).setLabel("Project type").setRequired(true);
      if (uiContext.getProvider().isGUI())
      {
         type.setItemLabelConverter((source) -> source.getType());
      }

      // Add Project types
      List<ProjectType> projectTypes = new ArrayList<>();
      for (ProjectType projectType : type.getValueChoices())
      {
         boolean buildable = false;
         for (ProjectProvider buildSystem : SimpleContainer.getServices(getClass().getClassLoader(),
                  ProjectProvider.class))
         {
            if (projectType.isEnabled(uiContext) && isProjectTypeBuildable(projectType, buildSystem))
            {
               projectTypes.add(projectType);
               buildable = true;
               break;
            }
         }
         if (!buildable)
         {
            if (log.isLoggable(Level.FINE))
            {
               log.log(Level.FINE,
                        "ProjectType ["
                                 + projectType.getType()
                                 + "] "
                                 + "deactivated because it cannot be built with any registered ProjectProvider instances ["
                                 + SimpleContainer.getServices(getClass().getClassLoader(), ProjectProvider.class)
                                 + "].");
            }
         }
      }
      Collections.sort(projectTypes, (left, right) -> left.priority() - right.priority());
      if (!projectTypes.isEmpty())
      {
         type.setDefaultValue(projectTypes.get(0));
      }
      type.setValueChoices(projectTypes);
   }

   private void configureTopLevelPackageInput(InputComponentFactory factory)
   {
      topLevelPackage = factory.createInput("topLevelPackage", String.class).setLabel("Top level package");
      topLevelPackage.setDefaultValue(new Callable<String>()
      {
         @Override
         public String call() throws Exception
         {
            String result = named.getValue();
            if (result != null)
            {
               result = ("org." + result).replaceAll("\\W+", ".");
               result = result.trim();
               result = result.replaceAll("^\\.", "");
               result = result.replaceAll("\\.$", "");
            }
            else
               result = "org.example";
            return result;
         }
      });
   }

   private void configureBuildSystemInput(InputComponentFactory factory, final UIContext uiContext)
   {
      final Imported<ProjectProvider> buildSystems = SimpleContainer.getServices(getClass().getClassLoader(),
               ProjectProvider.class);
      buildSystem = factory.createSelectOne("buildSystem", ProjectProvider.class).setLabel("Build system")
               .setRequired(true).setItemLabelConverter((source) -> source.getType());
      buildSystem.setValueChoices(new Callable<Iterable<ProjectProvider>>()
      {
         @Override
         public Iterable<ProjectProvider> call() throws Exception
         {
            List<ProjectProvider> result = new ArrayList<>();
            for (ProjectProvider buildSystemType : buildSystems)
            {
               ProjectType projectType = type.getValue();
               if (projectType != null)
               {
                  if (projectType.isEnabled(uiContext)
                           && isProjectTypeBuildable(projectType, buildSystemType))
                     result.add(buildSystemType);
               }
               else
                  result.add(buildSystemType);
            }

            Collections.sort(result, (left, right) -> left.priority() - right.priority());

            return result;
         }
      });

      buildSystem.setDefaultValue(new Callable<ProjectProvider>()
      {
         @Override
         public ProjectProvider call() throws Exception
         {
            Iterator<ProjectProvider> iterator = buildSystem.getValueChoices().iterator();
            if (iterator.hasNext())
            {
               return iterator.next();
            }
            return null;
         }
      });
   }

   private void configureStack(InputComponentFactory factory, final UIContext context)
   {
      NoStackFacet defaultStack = SimpleContainer.getServices(getClass().getClassLoader(), NoStackFacet.class)
               .get();
      Imported<StackFacet> stacks = SimpleContainer.getServices(getClass().getClassLoader(), StackFacet.class);
      final List<StackFacet> list = Lists.toList(stacks);
      Collections.sort(list, new StackFacetComparator());
      stack = factory.createSelectOne("stack", StackFacet.class)
               .setLabel("Stack")
               .setDescription("The technology stack to be used in this project")
               .setValueChoices(() -> list.stream()
                        .filter((stackFacet) -> (type.hasValue() || type.hasDefaultValue())
                                 && type.getValue().supports(stackFacet.getStack()))
                        .collect(Collectors.toSet()))
               // Enable stack field only if any stack is available
               .setEnabled(() -> Sets.toSet(stack.getValueChoices()).size() > 1)
               .setDefaultValue(defaultStack)
               .setItemLabelConverter((facet) -> context.getProvider().isGUI() ? facet.getStack().getName()
                        : Commands.shellifyOptionValue(facet.getStack().getName()));
   }

   private boolean isProjectTypeBuildable(ProjectType type, ProjectProvider buildSystem)
   {
      boolean result = false;
      Iterable<Class<? extends ProvidedProjectFacet>> requiredFacets = getRequiredBuildSystemFacets(type);
      if (requiredFacets == null || !requiredFacets.iterator().hasNext())
      {
         result = true;
      }
      else
      {
         for (Class<? extends ProvidedProjectFacet> required : requiredFacets)
         {
            result = false;
            for (Class<? extends ProvidedProjectFacet> provided : buildSystem.getProvidedFacetTypes())
            {
               if (provided.isAssignableFrom(required))
                  result = true;
            }
            if (!result)
               break;
         }
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   private Iterable<Class<? extends ProvidedProjectFacet>> getRequiredBuildSystemFacets(ProjectType type)
   {
      Set<Class<? extends ProvidedProjectFacet>> result = new HashSet<>();
      Iterable<Class<? extends ProjectFacet>> requiredFacets = type.getRequiredFacets();
      if (requiredFacets != null)
      {
         for (Class<? extends ProjectFacet> facetType : requiredFacets)
         {
            if (ProvidedProjectFacet.class.isAssignableFrom(facetType))
            {
               result.add((Class<? extends ProvidedProjectFacet>) facetType);
            }
         }
      }
      return result;
   }

   @Override
   public void validate(UIValidationContext context)
   {
      String packg = topLevelPackage.getValue();
      if (packg != null && !VALID_PACKAGE_PATTERN.matcher(packg).matches())
      {
         context.addValidationError(topLevelPackage, "Top level package must be a valid package name.");
      }

      if (overwrite.isEnabled() && !overwrite.getValue())
      {
         String errorMessage = String.format("Project location '%s' is not empty.", getTargetDirectory());
         context.addValidationError(targetLocation, errorMessage);
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Result result = Results.success("Project named '" + named.getValue() + "' has been created.");
      DirectoryResource targetDir = getTargetDirectory();
      boolean overwriteDir = overwrite.getValue() || useTargetLocationRoot.getValue();
      if (targetDir.mkdirs() || overwriteDir)
      {
         ProjectType value = type.getValue();
         ProjectFactory projectFactory = SimpleContainer.getServices(getClass().getClassLoader(), ProjectFactory.class)
                  .get();
         ProjectProvider buildSystemValue = buildSystem.getValue();
         Project project = projectFactory.createProject(targetDir, buildSystemValue);
         if (project != null)
         {
            UIContext uiContext = context.getUIContext();
            MetadataFacet metadataFacet = project.getFacet(MetadataFacet.class);
            metadataFacet.setProjectName(named.getValue());
            metadataFacet.setProjectVersion(version.getValue());
            metadataFacet.setProjectGroupName(topLevelPackage.getValue());
            if (project.hasFacet(PackagingFacet.class))
            {
               PackagingFacet packagingFacet = project.getFacet(PackagingFacet.class);
               if (finalName.hasValue())
               {
                  packagingFacet.setFinalName(finalName.getValue());
               }
               else
               {
                  packagingFacet.setFinalName(named.getValue());
               }
            }
            // Install the required facets
            FacetFactory facetFactory = SimpleContainer
                     .getServices(getClass().getClassLoader(), FacetFactory.class).get();
            if (value != null)
            {
               Iterable<Class<? extends ProjectFacet>> requiredFacets = value.getRequiredFacets();
               if (requiredFacets != null)
               {
                  for (Class<? extends ProjectFacet> facet : requiredFacets)
                  {
                     Class<? extends ProjectFacet> buildSystemFacet = buildSystemValue.resolveProjectFacet(facet);
                     if (!project.hasFacet(buildSystemFacet))
                     {
                        facetFactory.install(project, buildSystemFacet);
                     }
                     facetFactory.install(project, stack.getValue());

                  }
               }
            }
            // Install the selected facet
            if (stack.isEnabled() && stack.hasValue())
            {
               StackFacet stackFacet = stack.getValue();
               if (!(stackFacet instanceof NoStackFacet))
               {
                  if (facetFactory.install(project, stackFacet))
                  {
                     result = Results.aggregate(
                              Arrays.asList(result,
                                       Results.success("Stack '" + stackFacet.getStack().getName()
                                                + "' installed in project")));
                  }
               }
            }
            uiContext.setSelection(project.getRoot());
            uiContext.getAttributeMap().put(Project.class, project);
         }
         else
            result = Results.fail("Could not create project of type: [" + value + "]");
      }
      else
         result = Results.fail("Could not create target location: " + targetDir);

      return result;
   }

   private DirectoryResource getTargetDirectory()
   {
      DirectoryResource directory = targetLocation.getValue();
      DirectoryResource targetDir;
      if (useTargetLocationRoot.getValue() || named.getValue() == null)
      {
         targetDir = directory;
      }
      else
      {
         targetDir = directory.getChildDirectory(named.getValue());
      }
      return targetDir;
   }

   public UIInput<String> getNamed()
   {
      return named;
   }

   public UIInput<DirectoryResource> getTargetLocation()
   {
      return targetLocation;
   }

   public UIInput<Boolean> getOverwrite()
   {
      return overwrite;
   }

   public UISelectOne<ProjectType> getType()
   {
      return type;
   }

   public SingleValued<UIInput<String>, String> getTopLevelPackage()
   {
      return topLevelPackage;
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      ProjectType nextStep = type.getValue();
      if (nextStep != null)
      {
         return nextStep.next(context);
      }
      else
      {
         return null;
      }
   }
}

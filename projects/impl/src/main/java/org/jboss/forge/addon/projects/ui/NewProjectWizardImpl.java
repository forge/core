package org.jboss.forge.addon.projects.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ProjectProvider;
import org.jboss.forge.addon.projects.ProjectType;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.SingleValued;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

public class NewProjectWizardImpl implements UIWizard, NewProjectWizard
{
   private static final Logger log = Logger.getLogger(NewProjectWizardImpl.class.getName());

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private Imported<ProjectProvider> buildSystems;

   @Inject
   @WithAttributes(label = "Project name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Top level package")
   private UIInput<String> topLevelPackage;

   @Inject
   @WithAttributes(label = "Version")
   private UIInput<String> version;

   @Inject
   @WithAttributes(label = "Final name")
   private UIInput<String> finalName;

   @Inject
   @WithAttributes(label = "Project location")
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   @WithAttributes(label = "Overwrite existing project location")
   private UIInput<Boolean> overwrite;

   @Inject
   @WithAttributes(label = "Project type", required = true)
   private UISelectOne<ProjectType> type;

   @Inject
   @WithAttributes(label = "Build system", required = true)
   private UISelectOne<ProjectProvider> buildSystem;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Project: New").description("Create a new project")
               .category(Categories.create("Project", "Generation"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return buildSystem.getValueChoices().iterator().hasNext();
   }

   @Override
   public void initializeUI(final UIBuilder builder) throws Exception
   {
      configureProjectNamedInput();
      configureVersionInput();
      configureTargetLocationInput(builder);
      configureOverwriteInput();
      configureProjectTypeInput(builder);
      configureTopLevelPackageInput();
      configureBuildSystemInput();

      builder.add(named).add(topLevelPackage).add(version).add(finalName).add(targetLocation).add(overwrite).add(type)
               .add(buildSystem);
   }

   private void configureProjectNamedInput()
   {
      named.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext context)
         {
            if (named.getValue() != null && named.getValue().matches(".*[^-_.a-zA-Z0-9].*"))
               context.addValidationError(named, "Project name must not contain spaces or special characters.");
         }
      });
   }

   private void configureVersionInput()
   {
      version.setDefaultValue("1.0.0-SNAPSHOT");
   }

   private void configureTargetLocationInput(final UIBuilder builder)
   {
      UISelection<Resource<?>> currentSelection = builder.getUIContext().getInitialSelection();
      if (!currentSelection.isEmpty())
      {
         Resource<?> resource = currentSelection.get();
         if (resource instanceof DirectoryResource)
         {
            targetLocation.setDefaultValue((DirectoryResource) resource);
         }
      }

      if (!targetLocation.hasDefaultValue())
      {
         targetLocation.setDefaultValue(resourceFactory.create(DirectoryResource.class,
                  OperatingSystemUtils.getUserHomeDir()));
      }
   }

   private void configureOverwriteInput()
   {
      overwrite.setDefaultValue(false).setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            String projectName = named.getValue();
            return targetLocation.getValue() != null && projectName != null
                     && targetLocation.getValue().getChild(projectName).exists()
                     && !targetLocation.getValue().getChild(projectName).listResources().isEmpty();
         }
      });
   }

   private void configureProjectTypeInput(final UIBuilder builder)
   {
      if (builder.getUIContext().getProvider().isGUI())
      {
         type.setItemLabelConverter(new Converter<ProjectType, String>()
         {
            @Override
            public String convert(ProjectType source)
            {
               return source == null ? null : source.getType();
            }
         });
      }

      // Add Project types
      List<ProjectType> projectTypes = new ArrayList<>();
      for (ProjectType projectType : type.getValueChoices())
      {
         boolean buildable = false;
         for (ProjectProvider buildSystem : buildSystems)
         {
            if (isProjectTypeBuildable(projectType, buildSystem))
            {
               projectTypes.add(projectType);
               buildable = true;
               break;
            }
         }

         if (!buildable)
            log.log(Level.FINE, "ProjectType [" + projectType.getType() + "] "
                     + "deactivated because it cannot be built with any registered ProjectProvider instances ["
                     + buildSystems + "].");
      }

      Collections.sort(projectTypes, new Comparator<ProjectType>()
      {
         @Override
         public int compare(ProjectType left, ProjectType right)
         {
            return new Integer(left.priority()).compareTo(right.priority());
         }
      });

      if (!projectTypes.isEmpty())
      {
         type.setDefaultValue(projectTypes.get(0));
      }
      type.setValueChoices(projectTypes);
   }

   private void configureTopLevelPackageInput()
   {
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

   private void configureBuildSystemInput()
   {
      buildSystem.setRequired(true);
      buildSystem.setItemLabelConverter(new Converter<ProjectProvider, String>()
      {
         @Override
         public String convert(ProjectProvider source)
         {
            return source == null ? null : source.getType();
         }
      });

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
                  if (isProjectTypeBuildable(projectType, buildSystemType))
                     result.add(buildSystemType);
               }
               else
                  result.add(buildSystemType);
            }

            Collections.sort(result, new Comparator<ProjectProvider>()
            {
               @Override
               public int compare(ProjectProvider left, ProjectProvider right)
               {
                  return new Integer(left.priority()).compareTo(right.priority());
               }
            });

            return result;
         }
      });

      buildSystem.setDefaultValue(new Callable<ProjectProvider>()
      {
         @Override
         public ProjectProvider call() throws Exception
         {
            if (buildSystem.getValueChoices().iterator().hasNext())
            {
               return buildSystem.getValueChoices().iterator().next();
            }
            return null;
         }
      });
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
      if (packg != null && !packg.matches("(?i)(~\\.)?([a-z0-9_]+\\.?)+[a-z0-9_]"))
      {
         context.addValidationError(topLevelPackage, "Top level package must be a valid package name.");
      }

      if (overwrite.isEnabled() && overwrite.getValue() == false)
      {
         String errorMessage = String.format("Project location '%s' is not empty.",
                  targetLocation.getValue().getChild(named.getValue()));
         context.addValidationError(targetLocation, errorMessage);
      }

   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Result result = Results.success("Project named '" + named.getValue() + "' has been created.");
      DirectoryResource directory = targetLocation.getValue();
      DirectoryResource targetDir = directory.getChildDirectory(named.getValue());

      if (targetDir.mkdirs() || overwrite.getValue())
      {
         ProjectType value = type.getValue();

         Project project = null;
         if (value != null)
         {
            project = projectFactory.createProject(targetDir, buildSystem.getValue(), value.getRequiredFacets());
         }
         else
         {
            project = projectFactory.createProject(targetDir, buildSystem.getValue());
         }

         if (project != null)
         {
            UIContext uiContext = context.getUIContext();
            MetadataFacet metadataFacet = project.getFacet(MetadataFacet.class);
            metadataFacet.setProjectName(named.getValue());
            metadataFacet.setProjectVersion(version.getValue());
            metadataFacet.setProjectGroupName(topLevelPackage.getValue());

            PackagingFacet packagingFacet = project.getFacet(PackagingFacet.class);
            if (finalName.hasValue())
            {
               packagingFacet.setFinalName(finalName.getValue());
            }
            else
            {
               packagingFacet.setFinalName(named.getValue());
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
      if (type.getValue() != null)
      {
         return Results.navigateTo(type.getValue().getSetupFlow());
      }
      else
      {
         return null;
      }
   }
}

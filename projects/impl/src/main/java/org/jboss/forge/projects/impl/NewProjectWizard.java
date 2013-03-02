package org.jboss.forge.projects.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.convert.Converter;
import org.jboss.forge.projects.Project;
import org.jboss.forge.projects.ProjectFactory;
import org.jboss.forge.projects.ProjectType;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.Resource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UISelection;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.input.UISelectOne;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Categories;
import org.jboss.forge.ui.util.Metadata;
import org.jboss.forge.ui.wizard.UIWizard;

public class NewProjectWizard implements UIWizard
{
   @Inject
   private AddonRegistry registry;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private UIInput<String> named;

   @Inject
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   private UIInput<Boolean> overwrite;

   @Inject
   private UISelectOne<ProjectType> type;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("New Project").description("Create a new project")
               .category(Categories.create("Project", "Generation"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(final UIBuilder builder) throws Exception
   {
      named.setLabel("Project name");
      named.setRequired(true);

      targetLocation.setLabel("Project location");

      UISelection<Resource<?>> currentSelection = builder.getUIContext().getInitialSelection();
      if (currentSelection != null)
      {
         Resource<?> resource = currentSelection.get();
         if (resource instanceof DirectoryResource)
         {
            targetLocation.setDefaultValue((DirectoryResource) resource);
         }
      }
      else
      {
         targetLocation.setDefaultValue(resourceFactory.create(DirectoryResource.class, new File("")));
      }
      overwrite.setLabel("Overwrite existing project location");
      overwrite.setDefaultValue(false).setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return targetLocation.getValue() != null
                     && targetLocation.getValue().exists()
                     && !targetLocation.getValue().listResources().isEmpty();
         }
      });

      type.setRequired(true);
      type.setItemLabelConverter(new Converter<ProjectType, String>()
      {
         @Override
         public String convert(ProjectType source)
         {
            return source == null ? null : source.getType();
         }
      });

      // Add Project types
      List<ProjectType> projectTypes = new ArrayList<ProjectType>();
      for (ExportedInstance<ProjectType> instance : registry.getExportedInstances(ProjectType.class))
      {
         projectTypes.add(instance.get());
      }
      type.setValueChoices(projectTypes);

      builder.add(named).add(targetLocation).add(overwrite).add(type);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      if (overwrite.isEnabled() && overwrite.getValue() == false)
      {
         context.addValidationError(targetLocation, "Target location is not empty.");
      }

   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      Result result = Results.success("New project has been created.");
      DirectoryResource directory = targetLocation.getValue();
      DirectoryResource targetDir = directory.getChildDirectory(named.getValue());

      if (targetDir.mkdirs() || overwrite.getValue())
      {
         Project project = projectFactory.createProject(targetDir, type.getValue());
         if (project != null)
            context.setAttribute(Project.class, project);
         else
            result = Results.fail("Could not create project of type: [" + type.getValue() + "]");
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

   @Override
   public NavigationResult next(UIContext context) throws Exception
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

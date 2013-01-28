package org.jboss.forge.projects.impl;

import java.io.File;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.projects.ProjectType;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.FileResource;
import org.jboss.forge.resource.ResourceFactory;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandID;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.SimpleUICommandID;

public class NewProjectCommand implements UICommand
{
   @Inject
   private ResourceFactory factory;

   @Inject
   private UIInput<String> named;

   @Inject
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   private UIInput<Boolean> overwrite;

   @Inject
   private UIInput<ProjectType> type;

   @Override
   public UICommandID getId()
   {
      return new SimpleUICommandID("New Project", "Create a new project");
   }

   @Override
   public void initializeUI(UIContext context) throws Exception
   {
      named.setLabel("Project name");
      named.setRequired(true);

      targetLocation.setLabel("Project location");
      targetLocation.setDefaultValue(new Callable<DirectoryResource>()
      {
         @Override
         public DirectoryResource call() throws Exception
         {
            // TODO Forge should probably have the concept of a "working directory" from which relative paths can be
            // formed in cases like this, where Forge may be started from a completely random directory.
            return factory.create(DirectoryResource.class, new File(""));
         }
      });

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

      type.setRequired(false);

      context.getUIBuilder().add(named).add(targetLocation).add(overwrite).add(type);
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
      DirectoryResource directory = targetLocation.getValue();
      DirectoryResource targetDir = directory.getChildDirectory(named.getValue());

      if (targetDir.mkdirs() || overwrite.getValue())
      {
         FileResource<?> pom = targetDir.getChild("pom.xml").reify(FileResource.class);
         pom.createNewFile();
         pom.setContents(getClass().getClassLoader().getResourceAsStream("/pom-template.xml"));

         targetDir.getChildDirectory("src/main/java").mkdirs();
         targetDir.getChildDirectory("src/main/resources").mkdirs();
         targetDir.getChildDirectory("src/test/java").mkdirs();
         targetDir.getChildDirectory("src/test/resources").mkdirs();
      }
      else
         return Results.fail("Could not create target location: " + targetDir);

      return Results.success("New project has been created.");
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

   public UIInput<ProjectType> getType()
   {
      return type;
   }
}

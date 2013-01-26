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
   private UIInput<DirectoryResource> targetDirectory;

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

      targetDirectory.setLabel("Project directory");
      targetDirectory.setDefaultValue(new Callable<DirectoryResource>()
      {
         @Override
         public DirectoryResource call() throws Exception
         {
            // TODO Forge should probably have the concept of a "working directory" from which relative paths can be
            // formed in cases like this, where Forge may be started from a completely random directory.
            return factory.create(DirectoryResource.class, new File(named.getValue()));
         }
      });

      overwrite.setLabel("Overwrite existing project directory");
      overwrite.setDefaultValue(false).setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return targetDirectory.getValue() != null
                     && targetDirectory.getValue().exists()
                     && !targetDirectory.getValue().listResources().isEmpty();
         }
      });

      type.setRequired(false);

      context.getUIBuilder().add(named).add(targetDirectory).add(overwrite).add(type);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      if (overwrite.isEnabled() && overwrite.getValue() == false)
      {
         context.addValidationError(targetDirectory, "Target directory is not empty.");
      }

   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      DirectoryResource directory = targetDirectory.getValue();

      directory.mkdirs();

      FileResource<?> pom = directory.getChild("pom.xml").reify(FileResource.class);
      pom.createNewFile();
      pom.setContents(getClass().getClassLoader().getResourceAsStream("/pom-template.xml"));

      directory.getChildDirectory("src/main/java").mkdirs();
      directory.getChildDirectory("src/main/resources").mkdirs();
      directory.getChildDirectory("src/test/java").mkdirs();
      directory.getChildDirectory("src/test/resources").mkdirs();

      return Results.success();
   }

   /*
    * Getters
    */

   public UIInput<String> getNamed()
   {
      return named;
   }

   public UIInput<DirectoryResource> getTargetDirectory()
   {
      return targetDirectory;
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

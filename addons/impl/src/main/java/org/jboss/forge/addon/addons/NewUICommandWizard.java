/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons;

import java.util.Iterator;

import javax.inject.Inject;

import org.jboss.forge.addon.maven.projects.util.Packages;
import org.jboss.forge.addon.parser.java.JavaSourceFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class NewUICommandWizard extends AbstractProjectCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private JavaSourceFactory javaSourceFactory;

   @Inject
   @WithAttributes(label = "Command name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Target package", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   @WithAttributes(label = "Categories", required = false)
   private UIInputMany<String> categories;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(NewUICommandWizard.class)
               .name("Addon: New Command")
               .category(Categories.create("Addon", "Generate"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      Project project = getSelectedProject(builder.getUIContext());
      if (project == null)
      {
         UISelection<FileResource<?>> currentSelection = builder.getUIContext().getInitialSelection();
         if (!currentSelection.isEmpty())
         {
            FileResource<?> resource = currentSelection.get();
            while (!(resource instanceof DirectoryResource) && resource != null)
            {
               resource = resource.getParent();
            }
            if (resource != null)
               targetLocation.setDefaultValue((DirectoryResource) resource);
         }
      }
      else if (project.hasFacet(JavaSourceFacet.class))
      {
         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         targetLocation.setDefaultValue(facet.getSourceDirectory()).setEnabled(false);
         targetPackage.setValue(calculateModelPackage(project));
      }
      builder.add(targetLocation);
      builder.add(targetPackage).add(named).add(categories);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource;
      Project project = getSelectedProject(context);
      if (project == null)
      {
         JavaClass javaClass = createCommand(named.getValue(), targetPackage.getValue(), categories.getValue());
         javaResource = getJavaResource(targetLocation.getValue(), javaClass.getName());
         javaResource.setContents(javaClass);
      }
      else
      {
         final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
         JavaClass javaClass = createCommand(named.getValue(), targetPackage.getValue(), categories.getValue());
         javaResource = java.saveJavaSource(javaClass);
      }
      context.getUIContext().setSelection(javaResource);
      return Results.success("Command " + javaResource + " created");
   }

   private JavaClass createCommand(String commandName, String targetPackage, Iterable<String> categories)
   {
      // TODO Replace with Templates addon?
      JavaClass command = javaSourceFactory.create(JavaClass.class)
               .setName(commandName)
               .setPublic();

      if (targetPackage != null && !targetPackage.isEmpty())
         command.setPackage(targetPackage);

      command.setSuperType(AbstractUICommand.class);
      command.addImport(UIBuilder.class);
      command.addImport(UIContext.class);
      command.addImport(UIExecutionContext.class);
      command.addImport(UICommandMetadata.class);
      command.addImport(Metadata.class);
      command.addImport(Categories.class);
      command.addImport(Result.class);
      command.addImport(Results.class);

      Method<JavaClass> getMetadataMethod = command.addMethod()
               .setPublic()
               .setName("getMetadata")
               .setReturnType(UICommandMetadata.class)
               .setParameters("UIContext context");
      getMetadataMethod.addAnnotation(Override.class);

      String getMetadataMethodBody = "return Metadata.forCommand(" + command.getName() + ".class" + ")\n"
               + "\t.name(\"" + commandName + "\")";
      Iterator<String> iterator = categories.iterator();
      if (iterator.hasNext())
      {
         getMetadataMethodBody += "\t.category(Categories.create(";
         while (iterator.hasNext())
         {
            getMetadataMethodBody += "\"" + iterator.next() + "\"";
            if (iterator.hasNext())
               getMetadataMethodBody += ", ";
         }
         getMetadataMethodBody += "))";
      }
      getMetadataMethodBody += ";";
      getMetadataMethod.setBody(getMetadataMethodBody);

      command.addMethod()
               .setPublic()
               .setName("initializeUI")
               .setReturnTypeVoid()
               .setBody("// not implemented")
               .setParameters("UIBuilder builder")
               .addThrows(Exception.class)
               .addAnnotation(Override.class);

      command.addMethod()
               .setPublic()
               .setName("execute")
               .setReturnType(Result.class)
               .setParameters("UIExecutionContext context")
               .setBody("return Results.fail(\"Not implemented!\");")
               .addAnnotation(Override.class);

      // build the thing
      return command;
   }

   // TODO Should this be available in a utility?
   private JavaResource getJavaResource(final DirectoryResource sourceDir, final String relativePath)
   {
      String path = relativePath.trim().endsWith(".java")
               ? relativePath.substring(0, relativePath.lastIndexOf(".java")) : relativePath;
      path = path.replace(".", "/") + ".java";
      JavaResource target = sourceDir.getChildOfType(JavaResource.class, path);
      return target;
   }

   private String calculateModelPackage(Project project)
   {
      return Packages.toValidPackageName(project.getFacet(MetadataFacet.class).getTopLevelPackage()) + ".commands";
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}

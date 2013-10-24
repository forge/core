/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces.ui;

import java.io.FileNotFoundException;

import javax.inject.Inject;
import javax.persistence.Entity;

import org.jboss.forge.addon.javaee.faces.FacesOperations;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.parser.java.JavaSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NewConverterCommand extends AbstractJavaEECommand
{

   @Inject
   @WithAttributes(label = "Converter name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Target package", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   private FacesOperations facesOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Faces: New Converter")
               .description("Create a new JSF Converter Type")
               .category(Categories.create(super.getMetadata(context).getCategory(), "JSF"));
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
            if (resource instanceof DirectoryResource)
            {
               targetLocation.setDefaultValue((DirectoryResource) resource);
            }
            else
            {
               targetLocation.setDefaultValue(resource.getParent());
            }
         }
      }
      else if (project.hasFacet(JavaSourceFacet.class))
      {
         JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
         targetLocation.setDefaultValue(facet.getSourceFolder()).setEnabled(false);
         targetPackage.setValue(calculateConverterPackage(project));
      }
      builder.add(targetLocation);
      builder.add(targetPackage).add(named);
   }

   private String calculateConverterPackage(Project project)
   {
      final String[] value = new String[1];
      project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
      {
         @Override
         public void visit(JavaResource javaResource)
         {
            try
            {
               JavaSource<?> javaSource = javaResource.getJavaSource();
               if (javaSource.hasAnnotation(Entity.class))
               {
                  value[0] = javaSource.getPackage();
               }
            }
            catch (FileNotFoundException ignore)
            {
            }
         }
      });
      if (value[0] == null)
      {
         value[0] = project.getFacet(MetadataFacet.class).getTopLevelPackage() + ".convert";
      }
      return value[0];
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      String entityName = named.getValue();
      String entityPackage = targetPackage.getValue();
      DirectoryResource targetDir = targetLocation.getValue();
      Project project = getSelectedProject(context);
      JavaResource javaResource;
      if (project == null)
      {
         javaResource = facesOperations.newConverter(targetDir, entityName, entityPackage);
      }
      else
      {
         javaResource = facesOperations.newConverter(project, entityName, entityPackage);
      }
      context.setSelection(javaResource);
      return Results.success("Converter " + javaResource + " created");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }
}

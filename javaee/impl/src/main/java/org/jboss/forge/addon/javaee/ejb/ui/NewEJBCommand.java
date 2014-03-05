/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ejb.ui;

import java.io.FileNotFoundException;

import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.ejb.EJBOperations;
import org.jboss.forge.addon.javaee.ejb.EJBType;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.parser.java.JavaSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NewEJBCommand extends AbstractJavaEECommand implements UIWizard
{
   @Inject
   @WithAttributes(label = "Class name", description = "The simple name of the generated class", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Target package", type = InputType.JAVA_PACKAGE_PICKER)
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "Serializable", description = "If this EJB should implement the Serializable interface.", defaultValue = "true")
   private UIInput<Boolean> serializable;

   @Inject
   @WithAttributes(label = "Type", description = "Type type of EJB to be generated.")
   private UISelectOne<EJBType> type;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   private EJBOperations ejbOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("EJB: New Bean")
               .description("Create a new EJB")
               .category(Categories.create(super.getMetadata(context).getCategory(), "EJB"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      type.setDefaultValue(EJBType.STATELESS);
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
         targetLocation.setDefaultValue(facet.getSourceDirectory()).setEnabled(false);
         targetPackage.setValue(calculateServicePackage(project));
      }

      builder.add(targetLocation).add(targetPackage).add(named).add(type).add(serializable);
   }

   private String calculateServicePackage(Project project)
   {
      final String[] value = new String[1];
      project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
      {
         @Override
         public void visit(VisitContext context, JavaResource javaResource)
         {
            try
            {
               JavaSource<?> javaSource = javaResource.getJavaSource();
               if (javaSource.hasAnnotation(Stateless.class) || javaSource.hasAnnotation(Stateful.class)
                        || javaSource.hasAnnotation(MessageDriven.class) || javaSource.hasAnnotation(Singleton.class))
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
         value[0] = project.getFacet(MetadataFacet.class).getTopLevelPackage() + ".service";
      }
      return value[0];
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      String entityName = named.getValue();
      String entityPackage = targetPackage.getValue();
      EJBType ejbTypeChosen = type.getValue();
      DirectoryResource targetDir = targetLocation.getValue();
      JavaResource javaResource;

      Project project = getSelectedProject(context);
      if (project == null)
      {
         javaResource = ejbOperations.newEJB(targetDir, entityName, entityPackage, ejbTypeChosen,
                  serializable.getValue());
      }
      else
      {
         javaResource = ejbOperations
                  .newEJB(project, entityName, entityPackage, ejbTypeChosen, serializable.getValue());
      }

      context.getUIContext().getAttributeMap().put(JavaResource.class, javaResource);

      context.getUIContext().setSelection(javaResource);
      return Results.success("EJB " + javaResource + " created.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      if (EJBType.MESSAGEDRIVEN.equals(type.getValue()))
         return Results.navigateTo(NewMDBSetupStep.class);
      return null;
   }
}

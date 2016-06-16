/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ejb.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.ejb.EJBFacet;
import org.jboss.forge.addon.javaee.ejb.EJBOperations;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaMethodResource;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.Annotation;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.Method;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;

@StackConstraint(EJBFacet.class)
public class EJBSetMethodTransactionAttributeCommand extends AbstractJavaEECommand
{
   @Inject
   @WithAttributes(label = "Target EJB", description = "The EJB containing methods to be modified.", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetEjb;

   @Inject
   @WithAttributes(label = "Method", description = "The method on which the transaction type will be set.", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaMethodResource> method;

   @Inject
   @WithAttributes(label = "Transaction Type", description = "The type of the transaction", required = true)
   private UISelectOne<TransactionAttributeType> type;

   @Inject
   private EJBOperations ejbOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("EJB: Set Method Transaction Attribute")
               .description("Set the transaction type of a given EJB method")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "EJB"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupEJBs(builder.getUIContext());
      setupMethods(builder.getUIContext());
      builder.add(targetEjb).add(method).add(type);
   }

   private void setupMethods(UIContext uiContext)
   {
      method.setEnabled(() -> targetEjb.hasValue());
      method.setValueChoices(new Callable<Iterable<JavaMethodResource>>()
      {
         @Override
         public Iterable<JavaMethodResource> call() throws Exception
         {
            List<JavaMethodResource> result = new ArrayList<>();

            if (targetEjb.hasValue())
            {
               JavaResource source = targetEjb.getValue();
               for (Resource<?> resource : source.listResources())
               {
                  if (resource instanceof JavaMethodResource)
                  {
                     result.add((JavaMethodResource) resource);
                  }
               }
            }

            return result;
         }
      });

      method.setItemLabelConverter(JavaMethodResource::getName);
   }

   private void setupEJBs(UIContext context)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();
      Project project = getSelectedProject(context);
      final List<JavaResource> entities = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, JavaResource resource)
            {
               try
               {
                  JavaSource<?> source = resource.getJavaType();
                  if (ejbOperations.isEJB(resource.getJavaType()))
                  {
                     if (!((JavaClassSource) source).getMethods().isEmpty())
                        entities.add(resource);
                  }
               }
               catch (FileNotFoundException e)
               {
               }
            }
         });
      }
      targetEjb.setValueChoices(entities);
      int idx = -1;
      if (!selection.isEmpty())
      {
         idx = entities.indexOf(selection.get());
      }
      if (idx == -1)
      {
         idx = entities.size() - 1;
      }
      if (idx != -1)
      {
         targetEjb.setDefaultValue(entities.get(idx));
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaMethodResource resource = method.getValue();

      Method<?, ?> m = resource.getUnderlyingResourceObject();

      Annotation<?> annotation;
      if (m.hasAnnotation(TransactionAttribute.class))
      {
         annotation = m.getAnnotation(TransactionAttribute.class);
      }
      else
      {
         if (m instanceof MethodSource)
         {
            annotation = ((MethodSource<?>) m).addAnnotation(TransactionAttribute.class);
         }
         else
         {
            throw new IllegalStateException("Cannot add an annotation on a binary class");
         }
      }
      if (annotation instanceof AnnotationSource)
      {
         ((AnnotationSource<?>) annotation).setEnumValue(type.getValue());
      }

      JavaType<?> source = m.getOrigin();
      Resource<?> parent = resource.getParent();
      if (parent instanceof JavaResource && source instanceof JavaSource)
      {
         ((JavaResource) parent).setContents((JavaSource<?>) source);
      }

      return Results.success("Transaction attribute set to [" + type.getValue() + "]");
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      try
      {
         targetEjb.getValue().getJavaType();
      }
      catch (FileNotFoundException | NullPointerException e)
      {
         validator.addValidationError(targetEjb, "Type [" + targetEjb.getValue() + "] could not be found");
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}
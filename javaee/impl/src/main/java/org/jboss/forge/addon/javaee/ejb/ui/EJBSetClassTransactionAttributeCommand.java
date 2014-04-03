/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.ejb.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.FileResource;
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
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

public class EJBSetClassTransactionAttributeCommand extends AbstractJavaEECommand
{
   @Inject
   @WithAttributes(label = "Target EJB", description = "The EJB on which the transaction type will be set", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetEjb;

   @Inject
   @WithAttributes(label = "Transaction Type", description = "The type of the transaction", required = true)
   private UISelectOne<TransactionAttributeType> type;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("EJB: Set Class Transaction Attribute")
               .description("Set the transaction type of a given EJB")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "EJB"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupEntities(builder.getUIContext());
      builder.add(targetEjb).add(type);
   }

   private void setupEntities(UIContext context)
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
                  JavaType<?> javaType = resource.getJavaType();
                  if (
                  javaType.hasAnnotation(Stateless.class) ||
                           javaType.hasAnnotation(Stateful.class) ||
                           javaType.hasAnnotation(Singleton.class) ||
                           javaType.hasAnnotation(MessageDriven.class)
                  )
                  {
                     entities.add(resource);
                  }
               }
               catch (FileNotFoundException e)
               {
                  // ignore
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
      JavaResource resource = targetEjb.getValue();

      AnnotationSource<JavaClassSource> annotation;
      JavaClassSource ejb = resource.getJavaType();

      if (ejb.hasAnnotation(TransactionAttribute.class))
      {
         annotation = ejb.getAnnotation(TransactionAttribute.class);
      }
      else
      {
         annotation = ejb.addAnnotation(TransactionAttribute.class);
      }
      annotation.setEnumValue(type.getValue());

      resource.setContents(ejb);

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
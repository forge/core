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
import java.util.concurrent.Callable;

import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaMethodResource;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
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
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;

public class EJBSetMethodTransactionAttributeCommand extends AbstractJavaEECommand
{
   @Inject
   @WithAttributes(label = "EJB", description = "The EJB containing methods to be modified.", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> ejb;

   @Inject
   @WithAttributes(label = "Method", description = "The method on which the transaction type will be set.", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaMethodResource> method;

   @Inject
   @WithAttributes(label = "Transaction Type", description = "The type of the transacation", required = true)
   private UISelectOne<TransactionAttributeType> type;

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
      builder.add(ejb).add(method).add(type);
   }

   private void setupMethods(UIContext uiContext)
   {
      method.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return ejb.hasValue();
         }
      });
      method.setValueChoices(new Callable<Iterable<JavaMethodResource>>()
      {
         @Override
         public Iterable<JavaMethodResource> call() throws Exception
         {
            List<JavaMethodResource> result = new ArrayList<>();

            if (ejb.hasValue())
            {
               JavaResource source = ejb.getValue();
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

      method.setItemLabelConverter(new Converter<JavaMethodResource, String>()
      {
         @Override
         public String convert(JavaMethodResource source)
         {
            return source.getName();
         }
      });
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
                  JavaSource<?> source = resource.getJavaSource();
                  if (source instanceof JavaClass)
                  {
                     if (source.hasAnnotation(Stateless.class) || source.hasAnnotation(Stateful.class) ||
                              source.hasAnnotation(Singleton.class) || source.hasAnnotation(MessageDriven.class))
                     {
                        if (!((JavaClass) source).getMethods().isEmpty())
                           entities.add(resource);
                     }
                  }
               }
               catch (FileNotFoundException e)
               {
               }
            }
         });
      }
      ejb.setValueChoices(entities);
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
         ejb.setDefaultValue(entities.get(idx));
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaMethodResource resource = method.getValue();

      @SuppressWarnings("unchecked")
      Method<? extends JavaSource<?>> m = resource.getUnderlyingResourceObject();

      Annotation<? extends JavaSource<?>> annotation;
      if (m.hasAnnotation(TransactionAttribute.class))
      {
         annotation = m.getAnnotation(TransactionAttribute.class);
      }
      else
      {
         annotation = m.addAnnotation(TransactionAttribute.class);
      }
      annotation.setEnumValue(type.getValue());

      JavaSource<?> source = m.getOrigin();

      Resource<?> parent = resource.getParent();
      if (parent instanceof JavaResource)
         ((JavaResource) parent).setContents(source);

      return Results.success("Transaction attribute set to [" + type.getValue() + "]");
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      try
      {
         ejb.getValue().getJavaSource();
      }
      catch (FileNotFoundException e)
      {
         validator.addValidationError(ejb, "Type [" + ejb.getValue() + "] could not be found");
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }
}
/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.ejb.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.MessageDriven;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.ejb.EJBOperations;
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
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ActivationConfigPropertyAddCommand extends AbstractJavaEECommand
{
   @Inject
   @WithAttributes(label = "Target MDB", description = "The MDB on which the activation config will be set", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetMdb;

   @Inject
   @WithAttributes(label = "Property name", description = "The Activation config property name", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Property name", description = "The Activation config property name", required = true)
   private UIInput<String> value;

   @Inject
   private EJBOperations ejbOperations;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupMDBs(builder.getUIContext());
      builder.add(targetMdb).add(named).add(value);
   }

   private void setupMDBs(UIContext context)
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
                  if (source.hasAnnotation(MessageDriven.class))
                  {
                     entities.add(resource);
                  }
               }
               catch (FileNotFoundException e)
               {
               }
            }
         });
      }
      targetMdb.setValueChoices(entities);
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
         targetMdb.setDefaultValue(entities.get(idx));
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource resource = targetMdb.getValue();
      JavaClassSource target = resource.getJavaType();
      ejbOperations.addActivationConfigProperty(target, named.getValue(), value.getValue());
      resource.setContents(target);
      return Results.success();
   }

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("MDB: Add Activation Config Property")
               .description("Adds an @ActivationConfigProperty annotation to the given @MessageDriven EJB")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "EJB"));
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}

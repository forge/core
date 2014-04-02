/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi.ui;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Inject;
import javax.inject.Qualifier;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * Creates a new CDI Qualifier annotation
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NewQualifierCommand extends AbstractJavaSourceCommand
{
   @Inject
   @WithAttributes(label = "Inherited")
   private UIInput<Boolean> inherited;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Qualifier")
               .description("Creates a new CDI Qualifier annotation")
               .category(Categories.create(super.getMetadata(context).getCategory(), "CDI"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      builder.add(inherited);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      // TODO: Super implementation should have an "overwrite" flag for existing files?
      Result result = super.execute(context);
      if (!(result instanceof Failed))
      {
         JavaSourceFacet javaSourceFacet = getSelectedProject(context).getFacet(JavaSourceFacet.class);
         JavaResource javaResource = context.getUIContext().getSelection();
         JavaSource<?> qualifier = javaResource.getJavaType();
         qualifier.addAnnotation(Qualifier.class);
         if (inherited.getValue())
         {
            qualifier.addAnnotation(Inherited.class);
         }
         qualifier.addAnnotation(Retention.class).setEnumValue(RUNTIME);
         qualifier.addAnnotation(Target.class).setEnumValue(METHOD, FIELD, PARAMETER, TYPE);
         qualifier.addAnnotation(Documented.class);
         javaSourceFacet.saveJavaSource(qualifier);
      }
      return result;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected String getType()
   {
      return "CDI Qualifier";
   }

   @Override
   protected Class<? extends JavaSource<?>> getSourceType()
   {
      return JavaAnnotationSource.class;
   }

}

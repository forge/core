/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.validation.ui;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ReportAsSingleViolation;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * Creates a new Bean Validation constraint annotation
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class NewAnnotationCommand extends AbstractJavaSourceCommand
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
            .name("Constraint: New Annotation")
            .description("Create a Bean Validation constraint annotation")
            .category(Categories.create(super.getMetadata(context).getCategory(), "Bean Validation"));
   }

   @Override
  protected String getType() {
    return "Bean Validation Constraint Annotations";
  }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      Result result = super.execute(context);
      if (!(result instanceof Failed))
      {
         JavaSourceFacet javaSourceFacet = getSelectedProject(context).getFacet(JavaSourceFacet.class);
         JavaResource javaResource = context.getUIContext().getSelection();
         JavaAnnotationSource constraint = javaResource.getJavaType();
         // Constraint annotation header
         constraint.addAnnotation(Constraint.class).setStringValue("validatedBy = {}");
         constraint.addAnnotation(ReportAsSingleViolation.class);
         constraint.addAnnotation(Retention.class).setEnumValue(RUNTIME);
         constraint.addAnnotation(Target.class).setEnumValue(METHOD, FIELD, PARAMETER, TYPE, ANNOTATION_TYPE, CONSTRUCTOR);
         constraint.addAnnotation(Documented.class);
         // Constraint annotation body
         constraint.addAnnotationElement("String message() default \"Invalid value\"");
         constraint.addAnnotationElement("Class<?>[] groups() default { }");
         constraint.addAnnotationElement("Class<? extends Payload>[] payload() default { }");

         javaSourceFacet.saveJavaSource(constraint);
      }
      return result;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected Class<? extends JavaSource<?>> getSourceType()
   {
      return JavaAnnotationSource.class;
   }

  @Override
  protected String calculateDefaultPackage(UIContext context)
  {
    return getSelectedProject(context).getFacet(MetadataFacet.class).getTopLevelPackage() + ".constraints";
  }
}

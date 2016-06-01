/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import org.jboss.forge.addon.javaee.validation.ValidationFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;

/**
 * Creates a new Bean Validation constraint annotation
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@StackConstraint(ValidationFacet.class)
public class ValidationNewAnnotationCommandImpl extends AbstractValidationCommand<JavaAnnotationSource> implements
         ValidationNewAnnotationCommand
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Constraint: New Annotation")
               .description("Create a Bean Validation constraint annotation");
   }

   @Override
   protected String getType()
   {
      return "Constraint Annotation";
   }

   @Override
   protected Class<JavaAnnotationSource> getSourceType()
   {
      return JavaAnnotationSource.class;
   }

   @Override
   public JavaAnnotationSource decorateSource(UIExecutionContext context, Project project,
            JavaAnnotationSource constraint)
                     throws Exception
   {
      // Constraint annotation header
      constraint.addAnnotation(Constraint.class).setLiteralValue("validatedBy", "{}");
      constraint.addAnnotation(ReportAsSingleViolation.class);
      constraint.addAnnotation(Retention.class).setEnumValue(RUNTIME);
      constraint.addAnnotation(Target.class).setEnumValue(METHOD, FIELD, PARAMETER, TYPE, ANNOTATION_TYPE, CONSTRUCTOR);
      constraint.addAnnotation(Documented.class);
      constraint.addImport(Payload.class);

      // Constraint annotation body
      constraint.addAnnotationElement("String message() default \"Invalid value\"");
      constraint.addAnnotationElement("Class<?>[] groups() default { }");
      constraint.addAnnotationElement("Class<? extends Payload>[] payload() default { }");

      // Add nested annotation
      JavaAnnotationSource listNestedAnnotation = constraint.addNestedType(JavaAnnotationSource.class);
      listNestedAnnotation.setName("List");
      listNestedAnnotation.addAnnotation(Retention.class).setEnumValue(RUNTIME);
      listNestedAnnotation.addAnnotation(Target.class).setEnumValue(METHOD, FIELD, PARAMETER, TYPE, ANNOTATION_TYPE,
               CONSTRUCTOR);
      listNestedAnnotation.addAnnotationElement(constraint.getName() + "[] value()");
      return constraint;
   }
}

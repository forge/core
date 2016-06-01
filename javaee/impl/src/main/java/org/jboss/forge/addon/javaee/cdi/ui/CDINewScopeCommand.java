/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.NormalScope;
import javax.inject.Inject;
import javax.inject.Scope;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;

/**
 * Creates a new CDI Scope annotation
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CDINewScopeCommand extends AbstractCDICommand<JavaAnnotationSource>
{
   @Inject
   @WithAttributes(label = "Pseudo Scope")
   private UIInput<Boolean> pseudo;

   @Inject
   @WithAttributes(label = "Passivating")
   private UIInput<Boolean> passivating;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Scope")
               .description("Creates a new CDI Scope annotation");
   }

   @Override
   protected String getType()
   {
      return "CDI Scope";
   }

   @Override
   protected Class<JavaAnnotationSource> getSourceType()
   {
      return JavaAnnotationSource.class;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      builder.add(pseudo).add(passivating);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      Boolean pseudoValue = pseudo.getValue();
      Boolean passivatingValue = passivating.getValue();
      if (pseudoValue != null && passivatingValue != null)
      {
         if (pseudoValue && passivatingValue)
         {
            validator.addValidationError(passivating, "Cannot create a passivating pseudo-scope");
         }
      }
   }

   @Override
   public JavaAnnotationSource decorateSource(UIExecutionContext context, Project project, JavaAnnotationSource scope)
            throws Exception
   {
      if (pseudo.getValue())
      {
         scope.addAnnotation(Scope.class);
      }
      else
      {
         AnnotationSource<?> normalScope = scope.addAnnotation(NormalScope.class);
         if (passivating.getValue())
         {
            normalScope.setLiteralValue("passivating", Boolean.toString(true));
         }
      }
      scope.addAnnotation(Retention.class).setEnumValue(RUNTIME);
      scope.addAnnotation(Target.class).setEnumValue(TYPE, METHOD, FIELD);
      scope.addAnnotation(Documented.class);
      return scope;
   }
}

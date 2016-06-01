/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;

/**
 * Creates a new CDI Qualifier annotation
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CDINewQualifierCommand extends AbstractCDICommand<JavaAnnotationSource>
{
   @Inject
   @WithAttributes(label = "Inherited")
   private UIInput<Boolean> inherited;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Qualifier")
               .description("Creates a new CDI Qualifier annotation");
   }

   @Override
   protected String getType()
   {
      return "CDI Qualifier";
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
      builder.add(inherited);
   }

   @Override
   public JavaAnnotationSource decorateSource(UIExecutionContext context, Project project,
            JavaAnnotationSource qualifier) throws Exception
   {
      qualifier.addAnnotation(Qualifier.class);
      if (inherited.getValue())
      {
         qualifier.addAnnotation(Inherited.class);
      }
      qualifier.addAnnotation(Retention.class).setEnumValue(RUNTIME);
      qualifier.addAnnotation(Target.class).setEnumValue(METHOD, FIELD, PARAMETER, TYPE);
      qualifier.addAnnotation(Documented.class);
      return qualifier;
   }
}

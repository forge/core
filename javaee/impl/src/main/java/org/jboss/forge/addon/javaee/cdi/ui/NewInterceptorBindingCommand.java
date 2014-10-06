/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi.ui;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Inject;
import javax.interceptor.InterceptorBinding;

import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;

/**
 * Creates a new CDI Interceptor Binding annotation
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class NewInterceptorBindingCommand extends AbstractJavaSourceCommand<JavaAnnotationSource> implements
         PrerequisiteCommandsProvider
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Interceptor Binding")
               .description("Creates a new CDI Interceptor Binding annotation")
               .category(Categories.create(super.getMetadata(context).getCategory(), "CDI"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
   }

   @Override
   public JavaAnnotationSource decorateSource(UIExecutionContext context, Project project,
            JavaAnnotationSource interceptorBinding) throws Exception
   {
      interceptorBinding.addAnnotation(InterceptorBinding.class);
      interceptorBinding.addAnnotation(Retention.class).setEnumValue(RUNTIME);
      interceptorBinding.addAnnotation(Target.class).setEnumValue(METHOD, TYPE);
      interceptorBinding.addAnnotation(Documented.class);
      return interceptorBinding;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected String getType()
   {
      return "CDI Interceptor Binding";
   }

   @Override
   protected Class<JavaAnnotationSource> getSourceType()
   {
      return JavaAnnotationSource.class;
   }

   @Override
   public NavigationResult getPrerequisiteCommands(UIContext context)
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      Project project = getSelectedProject(context);
      if (project != null)
      {
         if (!project.hasFacet(CDIFacet.class))
         {
            builder.add(CDISetupCommand.class);
         }
      }
      return builder.build();
   }
}

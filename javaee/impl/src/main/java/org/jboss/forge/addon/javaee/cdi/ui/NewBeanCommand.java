/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi.ui;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Creates a new CDI Bean with a specific scope
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NewBeanCommand extends AbstractJavaSourceCommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "Scope", defaultValue = "DEPENDENT")
   private UISelectOne<BeanScope> scoped;

   @Inject
   @WithAttributes(label = "Custom Scope Annotation", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> customScopeAnnotation;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Bean")
               .description("Creates a new CDI Managed bean")
               .category(Categories.create(super.getMetadata(context).getCategory(), "CDI"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      Callable<Boolean> customScopeSelected = new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return scoped.getValue() == BeanScope.CUSTOM;
         }
      };
      customScopeAnnotation.setEnabled(customScopeSelected).setRequired(customScopeSelected);
      builder.add(scoped).add(customScopeAnnotation);
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      BeanScope scopedValue = scoped.getValue();
      if (BeanScope.CUSTOM == scopedValue)
      {
         source.addAnnotation(customScopeAnnotation.getValue());
      }
      else if (BeanScope.DEPENDENT != scopedValue)
      {
         source.addAnnotation(scopedValue.getAnnotation());
      }
      return source;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected String getType()
   {
      return "CDI Bean";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }
}

/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi.ui;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Creates a new CDI Decorator
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class CDINewDecoratorCommand extends AbstractCDICommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "Interface to delegate", required = true)
   private UIInput<String> delegate;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Decorator")
               .description("Creates a new CDI Decorator");
   }

    @Override
    protected String getType()
    {
        return "CDI Decorator";
    }

    @Override
    protected Class<JavaClassSource> getSourceType()
    {
        return JavaClassSource.class;
    }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      builder.add(delegate);
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project,
            JavaClassSource decorator) throws Exception
   {
      decorator.setAbstract(true).addInterface(delegate.getValue()).addAnnotation(Decorator.class);
      // Fields
      FieldSource<?> field = decorator.addField().setPrivate().setName("delegate").setType(delegate.getValue());
      field.addAnnotation(Inject.class);
      field.addAnnotation(Delegate.class);

      return decorator;
   }
}

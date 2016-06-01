/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import static org.jboss.forge.roaster.model.Visibility.PRIVATE;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class JPANewEntityListenerCommand extends AbstractJPACommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "Lifecycle", required = true)
   private UISelectMany<LifecycleType> lifecycles;

   @Inject
   private Inflector inflector;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("JPA: New Entity Listener")
               .description("Create a new JPA Entity Listener");
   }

   @Override
   protected String getType()
   {
      return "JPA Entity Listener";
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
      builder.add(lifecycles);
   }

   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      for (LifecycleType lifecyle : lifecycles.getValue())
      {
         String methodName = inflector.lowerCamelCase(lifecyle.getAnnotation().getSimpleName());
         source.addMethod().setName(methodName).setParameters("Object object").setReturnTypeVoid()
                  .setVisibility(PRIVATE).setBody("").addAnnotation(lifecyle.getAnnotation());
      }
      return source;
   }
}

/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi.ui;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Creates a new CDI Interceptor
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class CDINewInterceptorCommand extends AbstractCDICommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "Interceptor Binding", required = true)
   private UIInput<String> interceptorBinding;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Interceptor")
               .description("Creates a new CDI Interceptor");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      builder.add(interceptorBinding);
   }

   @Override
   protected String getType()
   {
      return "CDI Interceptor";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project,
            JavaClassSource interceptor) throws Exception
   {
      interceptor.addImport(interceptorBinding.getValue());
      interceptor.addAnnotation(interceptorBinding.getValue());
      interceptor.addAnnotation(Interceptor.class);
      interceptor.addImport(InvocationContext.class);
      interceptor.addMethod().setName("intercept").setParameters("InvocationContext ic").setReturnType(Object.class)
               .setPrivate()
               .addThrows(Exception.class).setBody(
                        "try {\n" +
                                 "            return ic.proceed();\n" +
                                 "        } finally {\n" +
                                 "        }").addAnnotation(AroundInvoke.class);
      return interceptor;
   }
}

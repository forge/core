/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_0;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.parser.java.converters.PackageRootConverter;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;

/**
 * Creates a new CDI Interceptor
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 * @author Martin Kouba
 */
public class CDINewInterceptorCommand extends AbstractEnablementCDICommand
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
      interceptorBinding.setValueConverter(new PackageRootConverter(getProjectFactory(), builder));
      builder.add(interceptorBinding);
      initializeEnablementUI(builder);
   }

   @Override
   protected String getType()
   {
      return "CDI Interceptor";
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project,
            JavaClassSource interceptor) throws Exception
   {
      super.decorateSource(context, project, interceptor);
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
                                 "        }")
               .addAnnotation(AroundInvoke.class);
      return interceptor;
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      checkEnablementConflict(validator,
               "Interceptor enabled for both the application and the bean archive (beans.xml) will only be invoked in the @Priority part of the chain");
   }

   @Override
   protected void enable(CDIFacet<?> facet, JavaClassSource source)
   {
      if (facet instanceof CDIFacet_1_0)
      {
         CDIFacet_1_0 cdiFacet_1_0 = (CDIFacet_1_0) facet;
         BeansDescriptor bd = cdiFacet_1_0.getConfig();
         bd.getOrCreateInterceptors().clazz(source.getQualifiedName());
         cdiFacet_1_0.saveConfig(bd);
      }
      else if (facet instanceof CDIFacet_1_1)
      {
         CDIFacet_1_1 cdiFacet_1_1 = (CDIFacet_1_1) facet;
         org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor bd = cdiFacet_1_1.getConfig();
         bd.getOrCreateInterceptors().clazz(source.getQualifiedName());
         cdiFacet_1_1.saveConfig(bd);
      }
   }

}

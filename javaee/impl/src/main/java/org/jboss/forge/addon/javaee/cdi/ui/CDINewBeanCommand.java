/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import java.io.Serializable;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_0;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;

/**
 * Creates a new CDI Bean with a specific scope
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CDINewBeanCommand extends AbstractEnablementCDICommand
{
   @Inject
   @WithAttributes(label = "Scope", defaultValue = "DEPENDENT")
   private UISelectOne<BeanScope> scoped;

   @Inject
   @WithAttributes(label = "Custom Scope Annotation", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> customScopeAnnotation;

   @Inject
   @WithAttributes(label = "Qualifier")
   private UIInput<String> qualifier;

   @Inject
   @WithAttributes(label = "Alternative")
   private UIInput<Boolean> alternative;

   @Inject
   @WithAttributes(label = "Named")
   private UIInput<Boolean> withNamed;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Bean")
               .description("Creates a new CDI Managed bean");
   }

   @Override
   protected String getType()
   {
      return "CDI Bean";
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
      builder.add(scoped).add(customScopeAnnotation).add(qualifier).add(alternative).add(withNamed);
      initializeEnablementUI(builder);
   }

   @Override
   protected Callable<Boolean> hasEnablement()
   {
      return () -> alternative.getValue();
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      super.decorateSource(context, project, source);
      BeanScope scopedValue = scoped.getValue();
      if (BeanScope.CUSTOM == scopedValue)
      {
         source.addAnnotation(customScopeAnnotation.getValue());
      }
      else if (BeanScope.DEPENDENT != scopedValue)
      {
         source.addAnnotation(scopedValue.getAnnotation());
         if (scopedValue.isSerializable())
         {
            source.addInterface(Serializable.class);
            source.addField().setPrivate().setStatic(true).setFinal(true).setName("serialVersionUID").setType("long")
                     .setLiteralInitializer("1L");
         }
      }
      else if (BeanScope.DEPENDENT == scopedValue && project.hasFacet(CDIFacet_1_1.class))
      {
         source.addAnnotation(scopedValue.getAnnotation());
      }
      if (withNamed.getValue())
      {
         source.addAnnotation(Named.class);
      }
      if (alternative.getValue())
      {
         source.addAnnotation(Alternative.class);
      }
      if (qualifier != null && qualifier.getValue() != null && !"".equals(qualifier.getValue()))
      {
         source.addAnnotation(qualifier.getValue());
      }
      return source;
   }

   @Override
   protected void enable(CDIFacet<?> facet, JavaClassSource source)
   {
      if (facet instanceof CDIFacet_1_0)
      {
         CDIFacet_1_0 cdiFacet_1_0 = (CDIFacet_1_0) facet;
         BeansDescriptor bd = cdiFacet_1_0.getConfig();
         bd.getOrCreateAlternatives().clazz(source.getQualifiedName());
         cdiFacet_1_0.saveConfig(bd);
      }
      else if (facet instanceof CDIFacet_1_1)
      {
         CDIFacet_1_1 cdiFacet_1_1 = (CDIFacet_1_1) facet;
         org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor bd = cdiFacet_1_1.getConfig();
         bd.getOrCreateAlternatives().clazz(source.getQualifiedName());
         cdiFacet_1_1.saveConfig(bd);
      }
   }

}

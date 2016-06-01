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
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Iterators;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;

/**
 * Creates a new CDI Stereotype annotation
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CDINewStereotypeCommand extends AbstractCDICommand<JavaAnnotationSource>
{
   @Inject
   @WithAttributes(label = "Inherited")
   private UIInput<Boolean> inherited;

   @Inject
   @WithAttributes(label = "Alternative")
   private UIInput<Boolean> alternative;

   @Inject
   @WithAttributes(label = "Named")
   private UIInput<Boolean> withNamed;

   @Inject
   @WithAttributes(label = "Target Element Types", required = true, requiredMessage = "No target element types selected")
   private UISelectMany<ElementType> targetTypes;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("CDI: New Stereotype")
               .description("Creates a new CDI Stereotype annotation");
   }

   @Override
   protected String getType()
   {
      return "CDI Stereotype";
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
      targetTypes.setValue(Arrays.asList(TYPE, METHOD, FIELD));
      builder.add(alternative).add(withNamed).add(inherited).add(targetTypes);
   }

   @Override
   public JavaAnnotationSource decorateSource(UIExecutionContext context, Project project,
            JavaAnnotationSource stereotype)
            throws Exception
   {
      stereotype.addAnnotation(Stereotype.class);
      if (inherited.getValue())
      {
         stereotype.addAnnotation(Inherited.class);
      }
      if (withNamed.getValue())
      {
         stereotype.addAnnotation(Named.class);
      }
      if (alternative.getValue())
      {
         stereotype.addAnnotation(Alternative.class);
      }
      stereotype.addAnnotation(Retention.class).setEnumValue(RUNTIME);
      Collection<ElementType> types = Iterators.asList(targetTypes.getValue());
      stereotype.addAnnotation(Target.class).setEnumValue(types.toArray(new ElementType[types.size()]));
      stereotype.addAnnotation(Documented.class);
      return stereotype;
   }
}

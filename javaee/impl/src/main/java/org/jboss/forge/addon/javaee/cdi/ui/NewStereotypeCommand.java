/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * Creates a new CDI Stereotype annotation
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NewStereotypeCommand extends AbstractJavaSourceCommand
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
               .description("Creates a new CDI Stereotype annotation")
               .category(Categories.create(super.getMetadata(context).getCategory(), "CDI"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      targetTypes.setValue(Arrays.asList(TYPE, METHOD, FIELD));
      builder.add(alternative).add(withNamed).add(inherited).add(targetTypes);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      // TODO: Super implementation should have an "overwrite" flag for existing files?
      Result result = super.execute(context);
      if (!(result instanceof Failed))
      {
         JavaSourceFacet javaSourceFacet = getSelectedProject(context).getFacet(JavaSourceFacet.class);
         JavaResource javaResource = context.getUIContext().getSelection();
         JavaSource<?> stereotype = javaResource.getJavaType();
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
         Collection<ElementType> types = toCollection(targetTypes.getValue());
         stereotype.addAnnotation(Target.class).setEnumValue(types.toArray(new ElementType[types.size()]));
         stereotype.addAnnotation(Documented.class);

         javaSourceFacet.saveJavaSource(stereotype);
      }
      return result;
   }

   private <T> Collection<T> toCollection(Iterable<T> iterable)
   {
      if (iterable == null)
      {
         return null;
      }
      else if (iterable instanceof Collection)
      {
         return (Collection<T>) iterable;
      }
      else
      {
         List<T> list = new ArrayList<>();
         for (T obj : iterable)
         {
            list.add(obj);
         }
         return list;
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected String getType()
   {
      return "CDI Stereotype";
   }

   @Override
   protected Class<? extends JavaSource<?>> getSourceType()
   {
      return JavaAnnotationSource.class;
   }

}

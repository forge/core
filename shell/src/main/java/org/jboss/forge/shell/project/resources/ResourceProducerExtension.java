/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project.resources;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResourceProducerExtension implements Extension
{
   private final Map<Class<?>, AnnotatedType<?>> typeOverrides = new HashMap<Class<?>, AnnotatedType<?>>();

   public <T> void processAnnotatedType(@Observes final ProcessAnnotatedType<T> event)
   {
      /**
       * Create a class to lazy load the builder, so it is not created unless needed. (Performance Fix) -- Mike Brock
       * (h/t to Stuart Douglas)
       */
      class BuilderHolder
      {
         private AnnotatedTypeBuilder<T> builder;

         public AnnotatedTypeBuilder<T> getBuilder()
         {
            if (builder == null)
            {
               builder = new AnnotatedTypeBuilder<T>();
               builder.readFromType(event.getAnnotatedType());
            }
            return builder;
         }
      }

      final BuilderHolder builderHolder = new BuilderHolder();

      boolean modifiedType = false;

      for (AnnotatedConstructor<T> c : event.getAnnotatedType().getConstructors())
      {
         if (c.isAnnotationPresent(Current.class))
         {
            for (AnnotatedParameter<?> p : c.getParameters())
            {
               if (p.getTypeClosure().contains(Resource.class))
               {
                  builderHolder.getBuilder().overrideConstructorParameterType(c.getJavaMember(), p.getPosition(),
                           Resource.class);
                  modifiedType = true;
               }
            }
         }
      }

      for (AnnotatedField<?> f : event.getAnnotatedType().getFields())
      {
         if (f.isAnnotationPresent(Current.class))
         {
            builderHolder.getBuilder().overrideFieldType(f.getJavaMember(), Resource.class);
            modifiedType = true;
         }
      }

      if (modifiedType)
      {
         AnnotatedType<T> replacement = builderHolder.getBuilder().create();
         typeOverrides.put(replacement.getJavaClass(), replacement);
         event.setAnnotatedType(replacement);
      }
   }
}

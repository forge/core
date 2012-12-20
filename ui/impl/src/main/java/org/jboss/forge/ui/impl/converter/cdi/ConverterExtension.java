/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl.converter.cdi;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.forge.ui.converter.Converter;
import org.jboss.forge.ui.impl.converter.ConverterRegistryImpl;

/**
 * Registers all the converters into the {@link ConverterRegistryImpl}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ConverterExtension implements Extension
{

   @SuppressWarnings("unchecked")
   <S, T> void observesConverters(@Observes ProcessAnnotatedType<? extends Converter<S, T>> pat) throws Exception
   {
      AnnotatedType<? extends Converter<S, T>> annotatedType = pat.getAnnotatedType();
      Class<?> javaClass = annotatedType.getJavaClass();
      try
      {
         Converter<S, T> newInstance = (Converter<S, T>) javaClass.newInstance();

         for (Method m : javaClass.getMethods())
         {
            // TODO: Ugly hack. It shoulld have a better way to discover the generic type used
            if ("convert".equals(m.getName()))
            {
               // TODO: Find the best way to create this object
               Class<S> sourceClass = (Class<S>) m.getParameterTypes()[0];
               Class<T> targetClass = (Class<T>) m.getReturnType();
               ConverterRegistryImpl.INSTANCE.addConverter(sourceClass, targetClass, newInstance);

               break;
            }
         }
      }
      catch (Exception e)
      {
         Logger.getLogger(getClass().getSimpleName())
                  .log(Level.FINE, "Error while instantiating " + javaClass, e);
      }
      // Always veto the instance
      pat.veto();
   }
}

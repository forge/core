/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.input.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.addon.ui.input.inject.InputComponentInjectionPoint;

/**
 * Default Implementation for {@link InputComponentInjectionPoint}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class DefaultInputComponentInjectionPoint implements InputComponentInjectionPoint
{
   private final InjectionPoint injectionPoint;

   public static DefaultInputComponentInjectionPoint of(InjectionPoint injectionPoint)
   {
      if (injectionPoint == null)
         return null;
      else
         return new DefaultInputComponentInjectionPoint(injectionPoint);
   }

   private DefaultInputComponentInjectionPoint(InjectionPoint injectionPoint)
   {
      super();
      this.injectionPoint = injectionPoint;
   }

   @Override
   public Class<?> getBeanClass()
   {
      return injectionPoint.getBean().getBeanClass();
   }

   @Override
   public Type getType()
   {
      return injectionPoint.getType();
   }

   @Override
   public Set<Annotation> getQualifiers()
   {
      return injectionPoint.getQualifiers();
   }

   @Override
   public Member getMember()
   {
      return injectionPoint.getMember();
   }

   @Override
   public boolean isDelegate()
   {
      return injectionPoint.isDelegate();
   }

   @Override
   public boolean isTransient()
   {
      return injectionPoint.isTransient();
   }
}
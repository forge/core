/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.furnace.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ExportedInstanceInjectionPoint implements InjectionPoint
{
   private final InjectionPoint wrapped;
   private final Set<Annotation> qualifiers = new HashSet<Annotation>();

   public ExportedInstanceInjectionPoint(final InjectionPoint wrapped, Annotation... qualifiers)
   {
      this.wrapped = wrapped;

      if (qualifiers != null)
         this.qualifiers.addAll(Arrays.asList(qualifiers));
   }

   @Override
   public Type getType()
   {
      return wrapped.getType();
   }

   @Override
   public boolean isDelegate()
   {
      return wrapped.isDelegate();
   }

   @Override
   public boolean isTransient()
   {
      return wrapped.isTransient();
   }

   @Override
   public Set<Annotation> getQualifiers()
   {
      return Collections.unmodifiableSet(qualifiers);
   }

   @Override
   public Bean<?> getBean()
   {
      return wrapped.getBean();
   }

   @Override
   public Member getMember()
   {
      return wrapped.getMember();
   }

   @Override
   public Annotated getAnnotated()
   {
      return wrapped.getAnnotated();
   }

   @Override
   public String toString()
   {
      return wrapped.toString();
   }
}

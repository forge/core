/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jboss.forge.furnace.util.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 *
 */
class ImmutableBean<T> extends AbstractImmutableBean<T>
{
   private final ContextualLifecycle<T> lifecycle;

   /**
    * Create a new, immutable bean. All arguments passed as collections are defensively copied.
    * 
    * @param beanClass The Bean class, may not be null
    * @param name The bean name
    * @param qualifiers The bean's qualifiers, if null, a singleton set of {@link javax.enterprise.inject.Default} is
    *           used
    * @param scope The bean's scope, if null, the default scope of {@link javax.enterprise.context.Dependent} is used
    * @param stereotypes The bean's stereotypes, if null, an empty set is used
    * @param types The bean's types, if null, the beanClass and {@link Object} will be used
    * @param alternative True if the bean is an alternative
    * @param nullable True if the bean is nullable
    * @param injectionPoints the bean's injection points, if null an empty set is used
    * @param toString the string which should be returned by #{@link #toString()}
    * @param contextualLifecycle Handler for {@link #create(javax.enterprise.context.spi.CreationalContext)} and
    *           {@link #destroy(Object, javax.enterprise.context.spi.CreationalContext)}
    * @throws IllegalArgumentException if the beanClass is null
    */
   // CHECKSTYLE:OFF
   public ImmutableBean(Class<?> beanClass, String name, Set<Annotation> qualifiers, Class<? extends Annotation> scope,
            Set<Class<? extends Annotation>> stereotypes, Set<Type> types, boolean alternative,
            boolean nullable, Set<InjectionPoint> injectionPoints, String toString,
            ContextualLifecycle<T> contextualLifecycle)
   {
      // CHECKSTYLE:ON
      super(beanClass, name, qualifiers, scope, stereotypes, types, alternative, nullable, injectionPoints, toString);
      this.lifecycle = contextualLifecycle;
   }

   @Override
   public T create(CreationalContext<T> creationalContext)
   {
      return lifecycle.create(this, creationalContext);
   }

   @Override
   public void destroy(T instance, CreationalContext<T> creationalContext)
   {
      this.lifecycle.destroy(this, instance, creationalContext);
   }
}

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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * An implementation of {@link ContextualLifecycle} that is backed by an {@link InjectionTarget}.
 * 
 * @param <T>
 */
class DelegatingContextualLifecycle<T> implements ContextualLifecycle<T>
{

   private final InjectionTarget<T> injectionTarget;

   /**
    * Instantiate a new {@link ContextualLifecycle} backed by an {@link InjectionTarget}.
    * 
    * @param injectionTarget the {@link InjectionTarget} used to create and destroy instances
    */
   public DelegatingContextualLifecycle(InjectionTarget<T> injectionTarget)
   {
      this.injectionTarget = injectionTarget;
   }

   @Override
   public T create(Bean<T> bean, CreationalContext<T> creationalContext)
   {
      T instance = injectionTarget.produce(creationalContext);
      injectionTarget.inject(instance, creationalContext);
      injectionTarget.postConstruct(instance);
      return instance;
   }

   @Override
   public void destroy(Bean<T> bean, T instance, CreationalContext<T> creationalContext)
   {
      try
      {
         injectionTarget.preDestroy(instance);
         creationalContext.release();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}

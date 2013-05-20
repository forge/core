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
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Injection target implementation that does nothing
 */
class DummyInjectionTarget<T> implements InjectionTarget<T>
{

   @Override
   public void inject(T instance, CreationalContext<T> ctx)
   {
   }

   @Override
   public void postConstruct(T instance)
   {
   }

   @Override
   public void preDestroy(T instance)
   {
   }

   @Override
   public void dispose(T instance)
   {
   }

   @Override
   public Set<InjectionPoint> getInjectionPoints()
   {
      return emptySet();
   }

   @Override
   public T produce(CreationalContext<T> ctx)
   {
      return null;
   }
}

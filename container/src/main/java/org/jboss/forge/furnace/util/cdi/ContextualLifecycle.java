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

/**
 * Callbacks used by {@link org.apache.deltaspike.core.util.bean.BeanBuilder} and
 * {@link org.apache.deltaspike.core.util.bean.ImmutableBeanWrapper} to allow control of the creation and destruction of
 * a custom bean.
 * 
 * @param <T> the class of the bean instance
 */
public interface ContextualLifecycle<T>
{
   /**
    * Callback invoked by a Solder created bean when {@link Bean#create(CreationalContext)} is called.
    * 
    * @param bean the bean initiating the callback
    * @param creationalContext the context in which this instance was created
    */
   T create(Bean<T> bean, CreationalContext<T> creationalContext);

   /**
    * Callback invoked by a Solder created bean when {@link Bean#destroy(Object, CreationalContext)} is called.
    * 
    * @param bean the bean initiating the callback
    * @param instance the contextual instance to destroy
    * @param creationalContext the context in which this instance was created
    */
   void destroy(Bean<T> bean, T instance, CreationalContext<T> creationalContext);

}

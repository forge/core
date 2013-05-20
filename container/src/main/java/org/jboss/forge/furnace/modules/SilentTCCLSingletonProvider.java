/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.furnace.modules;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.jboss.weld.bootstrap.api.Singleton;
import org.jboss.weld.bootstrap.api.SingletonProvider;
import org.jboss.weld.environment.se.Weld;

/**
 * Singleton provider that uses the Thread Context ClassLoader to differentiate between applications.
 * <p/>
 * (<b>Note:</b> Modified from {@link Weld} to remove System.out.println() call.)
 * 
 * @author Sanjeeb.Sahoo@Sun.COM
 * @author Pete Muir
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SilentTCCLSingletonProvider extends SingletonProvider
{
   private Map<Class<?>, Singleton<?>> store = new HashMap<Class<?>, Singleton<?>>();

   @Override
   @SuppressWarnings("unchecked")
   public <T> Singleton<T> create(Class<? extends T> type)
   {
      Singleton<?> singleton = store.get(type);
      if (singleton == null)
      {
         singleton = new TCCLSingleton<T>(type);
         store.put(type, singleton);
      }
      return (Singleton<T>) singleton;
   }

   private static class TCCLSingleton<T> implements Singleton<T>
   {
      // use Hashtable for concurrent access
      private final Map<ClassLoader, T> store = new Hashtable<ClassLoader, T>();
      private Class<? extends T> type;

      public TCCLSingleton(Class<? extends T> type)
      {
         this.type = type;
      }

      @Override
      public T get()
      {
         T instance = store.get(getClassLoader());
         if (instance == null)
         {
            throw new IllegalStateException("No instance of Singleton type [" + type.getName()
                     + "] found, for classloader key [" + getClassLoader() + "]");
         }
         return instance;
      }

      @Override
      public void set(T object)
      {
         store.put(getClassLoader(), object);
      }

      @Override
      public void clear()
      {
         store.remove(getClassLoader());
      }

      @Override
      public boolean isSet()
      {
         return store.containsKey(getClassLoader());
      }
   }

   private static ClassLoader getClassLoader()
   {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null)
      {
         return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>()
         {
            @Override
            public ClassLoader run()
            {
               return Thread.currentThread().getContextClassLoader();
            }
         });
      }
      else
      {
         return Thread.currentThread().getContextClassLoader();
      }
   }
}
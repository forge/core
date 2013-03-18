/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.forge.arquillian;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.jboss.arquillian.container.test.spi.ContainerMethodExecutor;
import org.jboss.arquillian.test.spi.TestMethodExecutor;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.TestResult.Status;
import org.jboss.forge.arquillian.protocol.ForgeProtocolConfiguration;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.Addon;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.lock.LockMode;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.container.util.ClassLoaders;

/**
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ForgeTestMethodExecutor implements ContainerMethodExecutor
{
   private Forge forge;

   public ForgeTestMethodExecutor(ForgeProtocolConfiguration config, final Forge forge)
   {
      if (config == null)
      {
         throw new IllegalArgumentException("ForgeProtocolConfiguration must be specified");
      }
      if (forge == null)
      {
         throw new IllegalArgumentException("Forge runtime must be provided");
      }
      this.forge = forge;
   }

   @Override
   public TestResult invoke(final TestMethodExecutor testMethodExecutor)
   {
      if (testMethodExecutor == null)
      {
         throw new IllegalArgumentException("TestMethodExecutor must be specified");
      }

      try
      {
         final String testClassName = testMethodExecutor.getInstance().getClass().getName();
         final AddonRegistry addonRegistry = forge.getAddonRegistry();

         final Object instance = forge.getLockManager().performLocked(LockMode.WRITE, new Callable<Object>()
         {
            @Override
            public Object call() throws Exception
            {
               Object result = null;
               for (Addon addon : addonRegistry.getRegisteredAddons())
               {
                  if (!addon.getStatus().isMissing()
                           && ClassLoaders.containsClass(addon.getClassLoader(), testClassName))
                  {
                     Future<Addon> future = addonRegistry.start(addon.getId());
                     future.get();
                  }

                  if (addon.getStatus().isStarted())
                  {
                     ServiceRegistry registry = addon.getServiceRegistry();
                     ExportedInstance<?> testInstance = registry.getExportedInstance(testClassName);

                     if (testInstance != null)
                     {
                        if (result == null)
                        {
                           result = testInstance.get();
                        }
                        else
                        {
                           throw new IllegalStateException(
                                    "Multiple test classes found in deployed addons. " +
                                             "You must have only one @Deployment(testable=true\"); deployment");
                        }
                     }
                  }
               }
               return result;
            }
         });

         if (instance == null)
            throw new IllegalStateException(
                     "Test runner could not locate test class in any deployment. "
                              + "Verify that your test case is deployed in an addon that supports remote " +
                              "services (Did you forget beans.xml in your deployment?)");

         TestResult result = null;
         try
         {
            Method method = instance.getClass().getMethod(testMethodExecutor.getMethod().getName());
            Annotation[] annotations = method.getAnnotations();

            for (Annotation annotation : annotations)
            {
               if ("org.junit.Ignore".equals(annotation.getClass().getName()))
               {
                  result = new TestResult(Status.SKIPPED);
               }
            }

            if (result == null)
            {
               try
               {
                  try
                  {
                     System.out.println("Executing test method: " + method);
                     method.invoke(instance);
                     result = new TestResult(Status.PASSED);
                  }
                  finally
                  {
                  }
               }
               catch (InvocationTargetException e)
               {
                  if (e.getCause() != null && e.getCause() instanceof Exception)
                     throw (Exception) e.getCause();
                  else
                     throw e;
               }
            }
         }
         catch (AssertionError e)
         {
            result = new TestResult(Status.FAILED, e);
         }
         catch (Exception e)
         {
            result = new TestResult(Status.FAILED, e);

            Throwable cause = e.getCause();
            while (cause != null)
            {
               if (cause instanceof AssertionError)
               {
                  result = new TestResult(Status.FAILED, cause);
                  break;
               }
               cause = cause.getCause();
            }
         }

         return result;
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Error launching test "
                  + testMethodExecutor.getInstance().getClass().getName() + "."
                  + testMethodExecutor.getMethod().getName() + "()", e);
      }
   }
}
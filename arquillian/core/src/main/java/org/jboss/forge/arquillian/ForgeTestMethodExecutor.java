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
import java.util.concurrent.Future;

import org.jboss.arquillian.container.test.spi.ContainerMethodExecutor;
import org.jboss.arquillian.test.spi.TestMethodExecutor;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.TestResult.Status;
import org.jboss.forge.arquillian.protocol.ForgeProtocolConfiguration;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.services.ExportedInstance;
import org.jboss.forge.furnace.services.ServiceRegistry;
import org.jboss.forge.furnace.util.Annotations;
import org.jboss.forge.furnace.util.ClassLoaders;

/**
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ForgeTestMethodExecutor implements ContainerMethodExecutor
{
   private Furnace forge;

   public ForgeTestMethodExecutor(ForgeProtocolConfiguration config, final Furnace forge)
   {
      if (config == null)
      {
         throw new IllegalArgumentException("ForgeProtocolConfiguration must be specified");
      }
      if (forge == null)
      {
         throw new IllegalArgumentException("Furnace runtime must be provided");
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

      Object testInstance = null;
      Class<?> testClass = null;
      try
      {
         final String testClassName = testMethodExecutor.getInstance().getClass().getName();
         final AddonRegistry addonRegistry = forge.getAddonRegistry();

         waitUntilStable(forge);
         System.out.println("Furnace stable, executing test.");

         for (Addon addon : addonRegistry.getAddons())
         {
            Future<Void> future = addon.getFuture();
            if (future != null)
               future.get();

            if (addon.getStatus().isStarted())
            {
               ServiceRegistry registry = addon.getServiceRegistry();
               ExportedInstance<?> exportedInstance = registry.getExportedInstance(testClassName);

               if (exportedInstance != null)
               {
                  if (testInstance == null)
                  {
                     testInstance = exportedInstance.get();
                     testClass = ClassLoaders.loadClass(addon.getClassLoader(), testClassName);
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
      }
      catch (Exception e)
      {
         String message = "Error launching test "
                  + testMethodExecutor.getInstance().getClass().getName() + "."
                  + testMethodExecutor.getMethod().getName() + "()";
         System.out.println(message);
         throw new IllegalStateException(message, e);
      }

      if (testInstance != null)
      {
         try
         {
            TestResult result = null;
            try
            {
               Method method = testInstance.getClass().getMethod(testMethodExecutor.getMethod().getName());
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
                        System.out.println("Executing test method: "
                                 + testMethodExecutor.getInstance().getClass().getName() + "."
                                 + testMethodExecutor.getMethod().getName() + "()");

                        invokeBefore(testClass, testInstance);
                        method.invoke(testInstance);
                        invokeAfter(testClass, testInstance);

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
            String message = "Error launching test "
                     + testMethodExecutor.getInstance().getClass().getName() + "."
                     + testMethodExecutor.getMethod().getName() + "()";
            System.out.println(message);
            throw new IllegalStateException(message, e);
         }
      }
      else
      {
         throw new IllegalStateException(
                  "Test runner could not locate test class in any deployment. "
                           + "Verify that your test case is deployed in an addon that supports remote " +
                           "services (Did you forget beans.xml in your deployment?)");
      }
   }

   @SuppressWarnings("unchecked")
   private void invokeBefore(Class<?> clazz, Object instance) throws Exception
   {
      if (clazz.getSuperclass() != null && !Object.class.equals(clazz.getSuperclass()))
         invokeBefore(clazz.getSuperclass(), instance);

      for (Method m : clazz.getMethods())
      {
         if (Annotations.isAnnotationPresent(m,
                  (Class<? extends Annotation>) clazz.getClassLoader().loadClass("org.junit.Before")))
         {
            m.invoke(instance);
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void invokeAfter(Class<?> clazz, Object instance) throws Exception
   {
      for (Method m : clazz.getMethods())
      {
         if (Annotations.isAnnotationPresent(m,
                  (Class<? extends Annotation>) clazz.getClassLoader().loadClass("org.junit.After")))
         {
            m.invoke(instance);
         }
      }

      if (clazz.getSuperclass() != null && !Object.class.equals(clazz.getSuperclass()))
         invokeAfter(clazz.getSuperclass(), instance);
   }

   private void waitUntilStable(Furnace forge) throws InterruptedException
   {
      while (forge.getStatus().isStarting())
      {
         Thread.sleep(10);
      }
   }
}
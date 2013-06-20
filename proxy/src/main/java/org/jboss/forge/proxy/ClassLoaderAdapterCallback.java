/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.proxy;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.jboss.forge.furnace.exception.ContainerException;
import org.jboss.forge.furnace.util.ClassLoaders;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClassLoaderAdapterCallback implements MethodHandler
{
   private static final ClassLoader JAVASSIST_LOADER = ProxyObject.class.getClassLoader();

   private final Object delegate;

   private final ClassLoader callingLoader;
   private final ClassLoader delegateLoader;

   private final Object unwrappedDelegate;
   private final Class<?> unwrappedDelegateType;
   private final ClassLoader unwrappedDelegateLoader;

   public ClassLoaderAdapterCallback(ClassLoader callingLoader, ClassLoader delegateLoader, Object delegate)
   {
      this.callingLoader = callingLoader;
      this.delegateLoader = delegateLoader;
      this.delegate = delegate;

      unwrappedDelegate = Proxies.unwrap(delegate);
      unwrappedDelegateType = Proxies.unwrapProxyTypes(unwrappedDelegate.getClass(), callingLoader, delegateLoader,
               unwrappedDelegate.getClass().getClassLoader());
      unwrappedDelegateLoader = unwrappedDelegateType.getClassLoader();
   }

   @Override
   public Object invoke(final Object obj, final Method thisMethod, final Method proceed, final Object[] args)
            throws Throwable
   {
      return ClassLoaders.executeIn(delegateLoader, new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            try
            {
               if (thisMethod.getDeclaringClass().equals(callingLoader.loadClass(ForgeProxy.class.getName())))
               {
                  return delegate;
               }
            }
            catch (Exception e)
            {
            }

            Method delegateMethod = getDelegateMethod(thisMethod);

            List<Object> parameterValues = convertParameterValues(args, delegateMethod);

            AccessibleObject.setAccessible(new AccessibleObject[] { delegateMethod }, true);
            try
            {
               Object result = delegateMethod.invoke(delegate, parameterValues.toArray());
               return enhanceResult(thisMethod, result);
            }
            catch (InvocationTargetException e)
            {
               if (e.getCause() instanceof Exception)
                  throw (Exception) e.getCause();
               throw e;
            }

         }

         private Method getDelegateMethod(final Method proxy) throws ClassNotFoundException, NoSuchMethodException
         {

            Method delegateMethod = null;
            try
            {
               List<Class<?>> parameterTypes = convertParameterTypes(proxy);
               delegateMethod = delegate.getClass().getMethod(proxy.getName(),
                        parameterTypes.toArray(new Class<?>[parameterTypes.size()]));
            }
            catch (ClassNotFoundException e)
            {
               method: for (Method m : delegate.getClass().getMethods())
               {
                  String methodName = proxy.getName();
                  String delegateMethodName = m.getName();
                  if (methodName.equals(delegateMethodName))
                  {
                     Class<?>[] methodParameterTypes = proxy.getParameterTypes();
                     Class<?>[] delegateParameterTypes = m.getParameterTypes();

                     if (methodParameterTypes.length == delegateParameterTypes.length)
                     {
                        for (int i = 0; i < methodParameterTypes.length; i++)
                        {
                           Class<?> methodType = methodParameterTypes[i];
                           Class<?> delegateType = delegateParameterTypes[i];

                           if (!methodType.getName().equals(delegateType.getName()))
                           {
                              continue method;
                           }
                        }

                        delegateMethod = m;
                        break;
                     }
                  }
               }
               if (delegateMethod == null)
                  throw e;
            }

            return delegateMethod;
         }
      });
   }

   private Object enhanceResult(final Method method, Object result)
   {
      if (result != null)
      {
         Object unwrappedResult = Proxies.unwrap(result);
         Class<?> unwrappedResultType = unwrappedResult.getClass();

         ClassLoader resultInstanceLoader = delegateLoader;
         if (!ClassLoaders.containsClass(delegateLoader, unwrappedResultType))
         {
            resultInstanceLoader = Proxies.unwrapProxyTypes(unwrappedResultType, callingLoader, delegateLoader,
                     unwrappedResultType.getClassLoader()).getClassLoader();
            // FORGE-928: java.util.ArrayList.class.getClassLoader() returns null
            if (resultInstanceLoader == null)
            {
               resultInstanceLoader = getClass().getClassLoader();
            }
         }

         Class<?> returnType = method.getReturnType();
         if (returnTypeNeedsEnhancement(returnType, result, unwrappedResultType))
         {
            Class<?>[] resultHierarchy = ProxyTypeInspector.getCompatibleClassHierarchy(callingLoader,
                     Proxies.unwrapProxyTypes(result.getClass(), callingLoader, delegateLoader, resultInstanceLoader));

            Class<?>[] returnTypeHierarchy = ProxyTypeInspector.getCompatibleClassHierarchy(callingLoader,
                     Proxies.unwrapProxyTypes(returnType, callingLoader, delegateLoader, resultInstanceLoader));

            if (!Modifier.isFinal(returnType.getModifiers()))
            {
               if (Object.class.equals(returnType) && !Object.class.equals(result))
               {
                  result = enhance(callingLoader, resultInstanceLoader, method, result, resultHierarchy);
               }
               else
               {
                  if (returnTypeHierarchy.length == 0)
                  {
                     returnTypeHierarchy = new Class[] { returnType };
                  }
                  result = enhance(callingLoader, resultInstanceLoader, method, result,
                           mergeHierarchies(returnTypeHierarchy, resultHierarchy));
               }
            }
            else
            {
               if (result.getClass().isEnum())
                  result = enhanceEnum(callingLoader, result);
               else
                  result = enhance(callingLoader, resultInstanceLoader, method, returnTypeHierarchy);
            }
         }
      }
      return result;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private Object enhanceEnum(ClassLoader loader, Object instance)
   {
      try
      {
         Class<Enum> callingType = (Class<Enum>) loader.loadClass(instance.getClass().getName());
         return Enum.valueOf(callingType, ((Enum) instance).name());
      }
      catch (ClassNotFoundException e)
      {
         throw new ContainerException(
                  "Could not enhance instance [" + instance + "] of type [" + instance.getClass() + "]", e);
      }
   }

   @SuppressWarnings("unchecked")
   private Class<?>[] mergeHierarchies(Class<?>[] left, Class<?>[] right)
   {
      for (Class<?> type : right)
      {
         boolean found = false;
         for (Class<?> existing : left)
         {
            if (type.equals(existing))
            {
               found = true;
               break;
            }
         }

         if (!found && type.isInterface())
            left = Arrays.append(left, type);
      }
      return left;
   }

   private boolean returnTypeNeedsEnhancement(Class<?> methodReturnType, Object returnValue,
            Class<?> unwrappedReturnValueType)
   {
      if (Proxies.isPassthroughType(unwrappedReturnValueType))
      {
         return false;
      }
      else if (!Object.class.equals(methodReturnType) && Proxies.isPassthroughType(methodReturnType))
      {
         return false;
      }

      if (unwrappedReturnValueType.getClassLoader() != null
               && !unwrappedReturnValueType.getClassLoader().equals(callingLoader))
      {
         if (ClassLoaders.containsClass(callingLoader, unwrappedReturnValueType)
                  && ClassLoaders.containsClass(callingLoader, methodReturnType))
         {
            return false;
         }
      }
      return true;
   }

   private List<Object> convertParameterValues(final Object[] args, Method delegateMethod)
   {
      List<Object> parameterValues = new ArrayList<Object>();
      for (int i = 0; i < delegateMethod.getParameterTypes().length; i++)
      {
         final Class<?> delegateParameterType = delegateMethod.getParameterTypes()[i];
         final Object parameterValue = args[i];

         if (parameterValue == null)
            parameterValues.add(null);
         else
         {
            if (parameterValue instanceof Class<?>)
            {
               Class<?> paramClassValue = (Class<?>) parameterValue;
               Class<?> loadedClass;
               try
               {
                  loadedClass = delegateLoader.loadClass(Proxies.unwrapProxyClassName(paramClassValue));
               }
               catch (ClassNotFoundException e)
               {
                  // Oh oh, there is no class with this type in the target.
                  // Trying with delegate ClassLoader;
                  try
                  {
                     loadedClass = unwrappedDelegateLoader.loadClass(Proxies.unwrapProxyClassName(paramClassValue));
                  }
                  catch (ClassNotFoundException cnfe)
                  {
                     /*
                      * No way, here is the original class and god bless you :) Also unwrap any proxy types since we
                      * don't know about this object, there is no reason to pass a proxied class type.
                      */
                     loadedClass = Proxies.unwrapProxyTypes(paramClassValue);
                  }
               }
               parameterValues.add(loadedClass);
            }
            else
            {
               Object unwrappedValue = Proxies.unwrapOnce(parameterValue);
               if (delegateParameterType.isAssignableFrom(unwrappedValue.getClass()) 
                        && !Proxies.isLanguageType(unwrappedValue.getClass()))
               {
                  // https://issues.jboss.org/browse/FORGE-939
                  parameterValues.add(unwrappedValue);
               }
               else
               {
                  unwrappedValue = Proxies.unwrap(parameterValue);
                  Class<?> unwrappedValueType = Proxies.unwrapProxyTypes(unwrappedValue.getClass(), delegateMethod
                           .getDeclaringClass().getClassLoader(), callingLoader,
                           delegateLoader, unwrappedValue.getClass()
                                    .getClassLoader());

                  ClassLoader valueDelegateLoader = delegateLoader;
                  ClassLoader methodLoader = delegateMethod.getDeclaringClass().getClassLoader();
                  if (methodLoader != null && ClassLoaders.containsClass(methodLoader, unwrappedValueType))
                  {
                     valueDelegateLoader = methodLoader;
                  }

                  ClassLoader valueCallingLoader = callingLoader;
                  if (!ClassLoaders.containsClass(callingLoader, unwrappedValueType))
                  {
                     valueCallingLoader = unwrappedValueType.getClassLoader();
                  }

                  // If it is a class, use the delegateLoader loaded version

                  if (delegateParameterType.isPrimitive())
                  {
                     parameterValues.add(parameterValue);
                  }
                  else if (delegateParameterType.isEnum())
                  {
                     parameterValues.add(enhanceEnum(methodLoader, parameterValue));
                  }
                  else
                  {
                     final Class<?> parameterType = parameterValue.getClass();
                     if ((!Proxies.isPassthroughType(delegateParameterType) && Proxies
                              .isLanguageType(delegateParameterType))
                              || !delegateParameterType.isAssignableFrom(parameterType))
                     {
                        Class<?>[] compatibleClassHierarchy = ProxyTypeInspector.getCompatibleClassHierarchy(
                                 valueDelegateLoader, unwrappedValueType);
                        if (compatibleClassHierarchy.length == 0)
                           compatibleClassHierarchy = new Class[] { delegateParameterType };
                        Object delegateParameterValue = enhance(valueDelegateLoader, valueCallingLoader,
                                 parameterValue,
                                 compatibleClassHierarchy);
                        parameterValues.add(delegateParameterValue);
                     }
                     else
                     {
                        parameterValues.add(unwrappedValue);
                     }
                  }
               }
            }
         }
      }
      return parameterValues;
   }

   private List<Class<?>> convertParameterTypes(final Method method) throws ClassNotFoundException
   {
      List<Class<?>> parameterTypes = new ArrayList<Class<?>>();
      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         Class<?> parameterType = method.getParameterTypes()[i];

         if (parameterType.isPrimitive())
         {
            parameterTypes.add(parameterType);
         }
         else
         {
            Class<?> delegateParameterType = delegateLoader.loadClass(parameterType.getName());
            parameterTypes.add(delegateParameterType);
         }
      }
      return parameterTypes;
   }

   public static <T> T enhance(final ClassLoader callingLoader, final ClassLoader delegateLoader,
            final Object delegate,
            final Class<?>... types)
   {
      return enhance(callingLoader, delegateLoader, null, delegate, types);
   }

   @SuppressWarnings("unchecked")
   private static <T> T enhance(final ClassLoader callingLoader, final ClassLoader delegateLoader,
            final Method sourceMethod,
            final Object delegate, final Class<?>... types)
   {
      // TODO consider removing option to set type hierarchy here. Instead it might just be
      // best to use type inspection of the given callingLoader ClassLoader to figure out the proper type.
      final Class<?> delegateType = delegate.getClass();

      try
      {
         return ClassLoaders.executeIn(JAVASSIST_LOADER, new Callable<T>()
         {
            @Override
            public T call() throws Exception
            {
               try
               {
                  Class<?>[] hierarchy = null;
                  if (types == null || types.length == 0)
                  {
                     hierarchy = ProxyTypeInspector.getCompatibleClassHierarchy(callingLoader,
                              Proxies.unwrapProxyTypes(delegateType, callingLoader, delegateLoader));
                     if (hierarchy == null || hierarchy.length == 0)
                     {
                        Logger.getLogger(getClass().getName()).fine(
                                 "Must specify at least one non-final type to enhance for Object: "
                                          + delegate + " of type " + delegate.getClass());

                        return (T) delegate;
                     }
                  }
                  else
                     hierarchy = Arrays.copy(types, new Class<?>[types.length]);

                  MethodFilter filter = new MethodFilter()
                  {
                     @Override
                     public boolean isHandled(Method method)
                     {
                        if (!method.getDeclaringClass().getName().contains("java.lang")
                                 || ("toString".equals(method.getName()) && method.getParameterTypes().length == 0))
                           return true;
                        return false;
                     }
                  };

                  Object enhancedResult = null;

                  ProxyFactory f = new ProxyFactory()
                  {
                     @Override
                     protected ClassLoader getClassLoader0()
                     {
                        ClassLoader result = callingLoader;
                        if (!ClassLoaders.containsClass(result, ProxyObject.class))
                           result = super.getClassLoader0();
                        return result;
                     };
                  };

                  f.setUseCache(true);

                  Class<?> first = hierarchy[0];
                  if (!first.isInterface())
                  {
                     f.setSuperclass(Proxies.unwrapProxyTypes(first, callingLoader, delegateLoader));
                     hierarchy = Arrays.shiftLeft(hierarchy, new Class<?>[hierarchy.length - 1]);
                  }

                  int index = Arrays.indexOf(hierarchy, ProxyObject.class);
                  if (index >= 0)
                  {
                     hierarchy = Arrays.removeElementAtIndex(hierarchy, index);
                  }

                  if (!Proxies.isProxyType(first) && !Arrays.contains(hierarchy, ForgeProxy.class))
                     hierarchy = Arrays.append(hierarchy, ForgeProxy.class);

                  if (hierarchy.length > 0)
                     f.setInterfaces(hierarchy);

                  f.setFilter(filter);
                  Class<?> c = f.createClass();
                  enhancedResult = c.newInstance();

                  try
                  {
                     ((ProxyObject) enhancedResult)
                              .setHandler(new ClassLoaderAdapterCallback(callingLoader, delegateLoader, delegate));
                  }
                  catch (ClassCastException e)
                  {
                     Class<?>[] interfaces = enhancedResult.getClass().getInterfaces();
                     for (Class<?> javassistType : interfaces)
                     {
                        if (ProxyObject.class.getName().equals(javassistType.getName()))
                        {
                           String callbackClassName = ClassLoaderAdapterCallback.class.getName();
                           ClassLoader javassistLoader = javassistType.getClassLoader();
                           Constructor<?> callbackConstructor = javassistLoader.loadClass(callbackClassName)
                                    .getConstructors()[0];

                           Class<?> typeArgument = javassistLoader.loadClass(MethodHandler.class.getName());
                           Method setHandlerMethod = javassistType.getMethod("setHandler", typeArgument);
                           setHandlerMethod.invoke(enhancedResult,
                                    callbackConstructor.newInstance(callingLoader, delegateLoader, delegate));
                        }
                     }
                  }

                  return (T) enhancedResult;
               }
               catch (Exception e)
               {
                  // Added try/catch for debug breakpoint purposes only.
                  throw e;
               }
            }

         });
      }
      catch (Exception e)
      {
         throw new ContainerException("Failed to create proxy for type [" + delegateType + "]", e);
      }
   }
}

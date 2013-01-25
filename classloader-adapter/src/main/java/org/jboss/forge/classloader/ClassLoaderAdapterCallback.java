package org.jboss.forge.classloader;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.util.Assert;
import org.jboss.forge.container.util.ClassLoaders;

public class ClassLoaderAdapterCallback implements MethodHandler
{
   private static final ClassLoader JAVASSIST_LOADER = ProxyObject.class.getClassLoader();

   private final ClassLoader fromLoader;
   private final ClassLoader toLoader;
   private final Object delegate;

   public ClassLoaderAdapterCallback(ClassLoader fromLoader, ClassLoader toLoader, Object delegate)
   {
      this.fromLoader = fromLoader;
      this.toLoader = toLoader;
      this.delegate = delegate;
   }

   @Override
   public Object invoke(final Object obj, final Method proxy, final Method method, final Object[] args)
            throws Throwable
   {
      return ClassLoaders.executeIn(toLoader, new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            try
            {
               Method delegateMethod = getDelegateMethod(proxy);

               List<Object> parameterValues = convertParameterValues(args, delegateMethod);

               AccessibleObject.setAccessible(new AccessibleObject[] { delegateMethod }, true);
               Object result = delegateMethod.invoke(delegate, parameterValues.toArray());

               return enhanceResult(proxy, result);
            }
            catch (Throwable e)
            {
               throw new ContainerException(
                        "Could not invoke proxy method [" + delegate.getClass().getName() + "."
                                 + proxy.getName() + "()] in ClassLoader ["
                                 + toLoader + "]", e);
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
         Class<?> returnType = method.getReturnType();
         if (needsEnhancement(result))
         {
            if (!Modifier.isFinal(returnType.getModifiers()))
            {
               if (Object.class.equals(returnType) && !Object.class.equals(result))
               {
                  result = enhance(fromLoader, toLoader, method, result,
                           ProxyTypeInspector.getCompatibleClassHierarchy(fromLoader,
                                    ProxyTypeInspector.unwrapProxyTypes(result.getClass(), fromLoader, toLoader)));
               }
               else
               {
                  result = enhance(fromLoader, toLoader, method, result, returnType);
               }
            }
            else
            {
               result = enhance(fromLoader, toLoader, method,
                        ProxyTypeInspector.getCompatibleClassHierarchy(fromLoader,
                                 ProxyTypeInspector.unwrapProxyTypes(returnType, fromLoader, toLoader)));
            }
         }
      }
      return result;
   }

   private boolean needsEnhancement(Object object)
   {
      Class<? extends Object> type = object.getClass();
      return !type.getName().contains("java.lang")
               && !type.isPrimitive();
   }

   private List<Object> convertParameterValues(final Object[] args, Method delegateMethod)
   {
      List<Object> parameterValues = new ArrayList<Object>();
      for (int i = 0; i < delegateMethod.getParameterTypes().length; i++)
      {
         final Class<?> delegateParameterType = delegateMethod.getParameterTypes()[i];
         final Object parameterValue = args[i];

         // If it is a class, use the toLoader loaded version
         if (parameterValue instanceof Class<?>)
         {
            Class<?> paramClassValue = (Class<?>) parameterValue;
            Class<?> loadedClass;
            try
            {
               loadedClass = toLoader.loadClass(paramClassValue.getName());
            }
            catch (ClassNotFoundException e)
            {
               // Oh oh, there is no class with this type in the target.
               // Trying with delegate ClassLoader;
               try
               {
                  loadedClass = delegate.getClass().getClassLoader().loadClass(paramClassValue.getName());
               }
               catch (ClassNotFoundException cnfe)
               {
                  // No way, here is the original class and god bless you :)
                  loadedClass = paramClassValue;
               }
            }
            parameterValues.add(loadedClass);
         }
         else if (delegateParameterType.isPrimitive() || parameterValue == null)
         {
            parameterValues.add(parameterValue);
         }
         else
         {
            final Class<?> parameterType = parameterValue.getClass();
            if (!delegateParameterType.isAssignableFrom(parameterType))
            {
               Object delegateParameterValue = enhance(toLoader, fromLoader, parameterValue,
                        delegateParameterType);
               parameterValues.add(delegateParameterValue);
            }
            else
            {
               parameterValues.add(parameterValue);
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
            Class<?> delegateParameterType = toLoader.loadClass(parameterType.getName());
            parameterTypes.add(delegateParameterType);
         }
      }
      return parameterTypes;
   }

   public static <T> T enhance(final ClassLoader fromLoader, final ClassLoader toLoader, final Object delegate,
            final Class<?>... types)
   {
      return enhance(fromLoader, toLoader, null, delegate, types);
   }

   @SuppressWarnings("unchecked")
   private static <T> T enhance(final ClassLoader fromLoader, final ClassLoader toLoader,
            final Method sourceMethod,
            final Object delegate, final Class<?>... types)
   {

      return ClassLoaders.executeIn(JAVASSIST_LOADER, new Callable<T>()
      {
         @Override
         public T call() throws Exception
         {
            Class<?>[] hierarchy = null;
            if (types == null || types.length == 0)
            {
               hierarchy = ProxyTypeInspector.getCompatibleClassHierarchy(toLoader,
                        ProxyTypeInspector.unwrapProxyTypes(delegate.getClass(), fromLoader, toLoader));
               if (hierarchy == null || hierarchy.length == 0)
                  throw new IllegalArgumentException("Must specify at least one non-final type to enhance for Object: "
                           + delegate);
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

            ProxyFactory f = new ProxyFactory();

            f.setUseCache(true);

            if (!hierarchy[0].isInterface())
            {
               f.setSuperclass(hierarchy[0]);
               hierarchy = Arrays.shiftLeft(hierarchy, new Class<?>[hierarchy.length - 1]);
            }

            if (hierarchy.length > 0)
               f.setInterfaces(hierarchy);

            f.setFilter(filter);
            Class<?> c = f.createClass();

            try
            {
               enhancedResult = c.newInstance();
            }
            catch (InstantiationException e)
            {
               throw new IllegalStateException(e);
            }
            catch (IllegalAccessException e)
            {
               throw new IllegalStateException(e);
            }

            try
            {
               ((ProxyObject) enhancedResult)
                        .setHandler(new ClassLoaderAdapterCallback(fromLoader, toLoader, delegate));
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
                              callbackConstructor.newInstance(fromLoader, toLoader, delegate));
                  }
               }
            }

            return (T) enhancedResult;
         }
      });
   }

   /*
    * Helper Types
    */

   static class ProxyTypeInspector
   {
      public static Class<?>[] getCompatibleClassHierarchy(ClassLoader loader, Class<?> origin)
      {
         Set<Class<?>> hierarchy = new LinkedHashSet<Class<?>>();

         Class<?> baseClass = origin;

         while (Modifier.isFinal(baseClass.getModifiers()))
         {
            baseClass = baseClass.getSuperclass();
         }

         if (ClassLoaders.containsClass(loader, baseClass)
                  && !Object.class.equals(baseClass)
                  && (isInstantiable(baseClass) || baseClass.isInterface()))
         {
            hierarchy.add(ClassLoaders.loadClass(loader, baseClass));
         }

         baseClass = origin;
         while (baseClass != null)
         {
            for (Class<?> type : baseClass.getInterfaces())
            {
               if (ClassLoaders.containsClass(loader, type))
                  hierarchy.add(ClassLoaders.loadClass(loader, type));
            }
            baseClass = baseClass.getSuperclass();
         }

         return hierarchy.toArray(new Class<?>[hierarchy.size()]);
      }

      private static boolean isInstantiable(Class<?> type)
      {
         try
         {
            type.getConstructor();
            return true;
         }
         catch (SecurityException e)
         {
         }
         catch (NoSuchMethodException e)
         {
         }
         return false;
      }

      /*
       * Helpers
       */

      private static Class<?> unwrapProxyTypes(Class<?> type, ClassLoader... loaders)
      {
         Class<?> result = type;

         for (ClassLoader loader : loaders)
         {
            try
            {
               if (result.getName().contains("$$EnhancerByCGLIB$$"))
               {
                  String typeName = result.getName().replaceAll("^(.*)\\$\\$EnhancerByCGLIB\\$\\$.*", "$1");
                  result = loader.loadClass(typeName);
                  break;
               }
               else if (result.getName().contains("_javassist_"))
               {
                  String typeName = result.getName().replaceAll("^(.*)_javassist_.*", "$1");
                  result = loader.loadClass(typeName);
                  break;
               }
            }
            catch (ClassNotFoundException e)
            {
            }
         }
         return result;
      }
   }

   static class Arrays
   {
      public static <ELEMENTTYPE> ELEMENTTYPE[] append(ELEMENTTYPE[] array, ELEMENTTYPE element)
      {
         final int length = array.length;
         array = java.util.Arrays.copyOf(array, length + 1);
         array[length] = element;
         return array;
      }

      public static <ELEMENTTYPE> ELEMENTTYPE[] copy(ELEMENTTYPE[] source, ELEMENTTYPE[] target)
      {
         Assert.isTrue(source.length == target.length, "Source and destination arrays must be of the same length.");
         System.arraycopy(source, 0, target, 0, source.length);
         return target;
      }

      public static <ELEMENTTYPE> ELEMENTTYPE[] shiftLeft(ELEMENTTYPE[] source, ELEMENTTYPE[] target)
      {
         Assert.isTrue(source.length > 0, "Source array length cannot be zero.");
         Assert.isTrue(source.length - 1 == target.length,
                  "Destination array must be one element shorter than the source array.");

         System.arraycopy(source, 1, target, 0, target.length);
         return target;
      }
   }
}

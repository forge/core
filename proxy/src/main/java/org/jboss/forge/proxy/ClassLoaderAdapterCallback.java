package org.jboss.forge.proxy;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.util.ClassLoaders;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ClassLoaderAdapterCallback implements MethodHandler
{
   private static final ClassLoader JAVASSIST_LOADER = ProxyObject.class.getClassLoader();

   private final ClassLoader fromLoader;
   private final ClassLoader toLoader;
   private final ClassLoader delegateLoader;
   private final Object delegate;

   private final Object unwrappedDelegate;
   private final Class<?> unwrappedDelegateType;

   public ClassLoaderAdapterCallback(ClassLoader fromLoader, ClassLoader toLoader, Object delegate)
   {
      this.fromLoader = fromLoader;
      this.toLoader = toLoader;
      this.delegate = delegate;

      unwrappedDelegate = Proxies.unwrap(delegate);
      unwrappedDelegateType = Proxies.unwrapProxyTypes(unwrappedDelegate.getClass(), fromLoader, toLoader,
               unwrappedDelegate.getClass().getClassLoader());
      delegateLoader = unwrappedDelegateType.getClassLoader();
   }

   @Override
   public Object invoke(final Object obj, final Method thisMethod, final Method proceed, final Object[] args)
            throws Throwable
   {
      return ClassLoaders.executeIn(toLoader, new Callable<Object>()
      {
         @Override
         public Object call() throws Exception
         {
            try
            {
               try
               {
                  if (thisMethod.getDeclaringClass().equals(fromLoader.loadClass(ForgeProxy.class.getName())))
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
               Object result = delegateMethod.invoke(delegate, parameterValues.toArray());

               return enhanceResult(thisMethod, result);
            }
            catch (Throwable e)
            {
               throw new ContainerException(
                        "Could not invoke proxy method [" + delegate.getClass().getName() + "."
                                 + thisMethod.getName() + "()] in ClassLoader ["
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
         Object unwrappedResult = Proxies.unwrap(result);
         Class<?> unwrappedResultType = unwrappedResult.getClass();

         ClassLoader resultToLoader = toLoader;
         if (!ClassLoaders.containsClass(toLoader, unwrappedResultType))
         {
            resultToLoader = Proxies.unwrapProxyTypes(unwrappedResultType, fromLoader, toLoader,
                     unwrappedResultType.getClassLoader()).getClassLoader();
         }

         Class<?> returnType = method.getReturnType();
         if (returnTypeNeedsEnhancement(returnType, result, unwrappedResultType))
         {
            Class<?>[] resultHierarchy = ProxyTypeInspector.getCompatibleClassHierarchy(fromLoader,
                     Proxies.unwrapProxyTypes(result.getClass(), fromLoader, toLoader, resultToLoader));

            Class<?>[] returnTypeHierarchy = ProxyTypeInspector.getCompatibleClassHierarchy(fromLoader,
                     Proxies.unwrapProxyTypes(returnType, fromLoader, toLoader, resultToLoader));

            if (!Modifier.isFinal(returnType.getModifiers()))
            {
               if (Object.class.equals(returnType) && !Object.class.equals(result))
               {
                  result = enhance(fromLoader, resultToLoader, method, result, resultHierarchy);
               }
               else
               {
                  if (returnTypeHierarchy.length == 0)
                  {
                     returnTypeHierarchy = new Class[] { returnType };
                  }
                  result = enhance(fromLoader, resultToLoader, method, result,
                           mergeHierarchies(returnTypeHierarchy, resultHierarchy));
               }
            }
            else
            {
               result = enhance(fromLoader, resultToLoader, method, returnTypeHierarchy);
            }
         }
      }
      return result;
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
      if (unwrappedReturnValueType.getName().contains("java.lang") || unwrappedReturnValueType.isPrimitive())
      {
         return false;
      }
      else if (!Object.class.equals(methodReturnType)
               && (methodReturnType.getName().startsWith("java.lang") || methodReturnType.isPrimitive()))
      {
         return false;
      }

      if (unwrappedReturnValueType.getClassLoader() != null
               && !unwrappedReturnValueType.getClassLoader().equals(fromLoader))
      {
         if (ClassLoaders.containsClass(fromLoader, unwrappedReturnValueType)
                  && ClassLoaders.containsClass(fromLoader, methodReturnType))
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
            Object unwrappedValue = Proxies.unwrap(parameterValue);
            Class<?> unwrappedValueType = Proxies.unwrapProxyTypes(unwrappedValue.getClass(), fromLoader, toLoader,
                     unwrappedValue.getClass().getClassLoader());

            ClassLoader valueFromLoader = fromLoader;
            if (!ClassLoaders.containsClass(fromLoader, unwrappedValueType))
            {
               valueFromLoader = unwrappedValueType.getClassLoader();
            }

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
                     loadedClass = delegateLoader.loadClass(paramClassValue.getName());
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
                  Object delegateParameterValue = enhance(toLoader, valueFromLoader, parameterValue,
                           delegateParameterType);
                  parameterValues.add(delegateParameterValue);
               }
               else
               {
                  parameterValues.add(Proxies.unwrap(parameterValue));
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
                        Proxies.unwrapProxyTypes(delegate.getClass(), fromLoader, toLoader));
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

            Class<?> first = hierarchy[0];
            if (!first.isInterface())
            {
               f.setSuperclass(Proxies.unwrapProxyTypes(first, fromLoader, toLoader));
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
}

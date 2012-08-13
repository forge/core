/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project.resources;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Current;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResourceProducer
{
   @Produces
   @Current
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public Resource getCurrentResource(final InjectionPoint ip, final Shell shell)
   {
      Resource<?> currentResource = shell.getCurrentResource();
      Type type = null;

      Member member = ip.getMember();
      if (member instanceof Field)
      {
         type = ((Field) member).getType();
      }
      else if (member instanceof Method)
      {
         AnnotatedParameter<?> annotated = (AnnotatedParameter<?>) ip.getAnnotated();
         type = annotated.getBaseType();
      }
      else if (member instanceof Constructor<?>)
      {
         AnnotatedParameter<?> annotated = (AnnotatedParameter<?>) ip.getAnnotated();
         type = annotated.getBaseType();
      }

      try
      {
         Class<? extends Resource> resourceClass = currentResource.getClass();
         if ((type instanceof Class) && ((Class) type).isAssignableFrom(resourceClass))
         {
            return currentResource;
         }
         else if (type instanceof ParameterizedType)
         {
            ParameterizedType t = (ParameterizedType) type;
            Type rawType = t.getRawType();
            if ((rawType instanceof Class) && ((Class) rawType).isAssignableFrom(resourceClass))
            {
               return currentResource;
            }
         }
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Could not @Inject Resource type into InjectionPoint:" + ip, e);
      }

      return null;
   }
}

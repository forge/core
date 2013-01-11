package org.jboss.forge.container.services;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import net.sf.cglib.proxy.Enhancer;

import org.jboss.forge.container.AddonRegistry;
import org.jboss.forge.container.exception.ContainerException;
import org.jboss.forge.container.impl.Service;

public class RemoteServiceProxyBeanProducerMethod
{
   @Produces
   @Service
   public static Object produceRemoteService(AddonRegistry registry, InjectionPoint ip)
   {
      if (ip == null)
         throw new IllegalStateException(
                  "Cannot perform dynamic lookup of @"
                           + Remote.class.getName()
                           + " instances - they must be injected directly into a field or as a method/constructor parameter.");

      Member member = ip.getMember();
      Class<?> type = null;
      if (member instanceof Method)
      {
         type = ((Method) member).getReturnType();
      }
      else if (member instanceof Field)
      {
         type = ((Field) member).getType();
      }
      else
         throw new ContainerException("Cannot handle producer for non-Field and non-Method member type: " + member);

      try
      {
         return Enhancer.create((Class<?>) type, new RemoteServiceProxyBeanCallback(registry, type));
      }
      catch (Exception e)
      {
         throw new ContainerException("Failed to proxy bean of type:" + type, e);
      }
   }
}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.services;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.inject.spi.ProcessProducer;

import org.jboss.forge.container.exception.ContainerException;

/**
 * One classloader/thread/weld container per plugin module. One primary executor container running, fires events to each
 * plugin-container.
 * 
 * Multi-threaded bootstrap. Loads primary container, then attaches individual plugin containers as they come up.
 * 
 * Ideas:
 * 
 * Addons may depend on other addons beans, but these beans must be explicitly exposed via the {@link Remote} and
 * {@link Service} API.
 */
public class ContainerServiceManager implements Extension
{

   public void processAnnotatedType(@Observes ProcessAnnotatedType<? extends Remote> event)
   {
      event.setAnnotatedType(new RemoteAnnotatedType(event.getAnnotatedType()));
   }

   public void processManagedBean(@Observes ProcessManagedBean<? extends Remote> event)
   {
   }

   /**
    * This occurs in the producing Module.
    */
   public void processProducer(@Observes ProcessProducer<?, ? extends Remote> event, BeanManager manager)
   {
      AnnotatedMember<?> annotatedMember = event.getAnnotatedMember();
      Member member = annotatedMember.getJavaMember();

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
         throw new ContainerException("Cannot handle producer for non-Field and non-Method member type");

      event.setProducer(new RemoteBeanProducer(manager, event.getProducer(), type));
   }
}

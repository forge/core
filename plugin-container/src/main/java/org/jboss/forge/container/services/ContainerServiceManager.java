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
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessProducer;

import org.jboss.forge.container.exception.ContainerException;

/**
 * One classloader/thread/weld container per plugin module. One primary executor container running, fires events to each
 * plugin-container.
 * 
 * Multi-threaded bootstrap. Loads primary container, then attaches individual plugin containers as they come up.
 * 
 * Prevents weld library conflicts.
 * 
 * Ideas:
 * 
 * Addons may depend on other addons beans, but these beans must be explicitly exposed via the {@link Remote} and
 * {@link Service} API.
 */
public class ContainerServiceManager implements Extension
{
   public void processBean(@Observes ProcessBean<?> event)
   {
   }

   public void processProducer(@Observes ProcessProducer<?, ? extends Remote> event)
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

      if (type != null)
         event.setProducer(new RemoteProducerWrapper(event.getProducer(), type));
   }
}

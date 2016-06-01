/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * Provides information about the injection point in a CDI-decoupled manner
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface InputComponentInjectionPoint
{
   /**
    * The bean {@linkplain Class class} of the managed bean or session bean or of the bean that declares the producer
    * method or field.
    * 
    * @return the bean {@linkplain Class class}
    */
   public Class<?> getBeanClass();

   /**
    * Get the required type of injection point.
    * 
    * @return the required type
    */
   public Type getType();

   /**
    * Get the required qualifiers of the injection point.
    * 
    * @return the required qualifiers
    */
   public Set<Annotation> getQualifiers();

   /**
    * Get the {@link java.lang.reflect.Field} object in the case of field injection, the
    * {@link java.lang.reflect.Method} object in the case of method parameter injection or the
    * {@link java.lang.reflect.Constructor} object in the case of constructor parameter injection.
    * 
    * @return the member
    */
   public Member getMember();

   /**
    * Determines if the injection point is a decorator delegate injection point.
    * 
    * @return <tt>true</tt> if the injection point is a decorator delegate injection point, and <tt>false</tt> otherwise
    */
   public boolean isDelegate();

   /**
    * Determines if the injection is a transient field.
    * 
    * @return <tt>true</tt> if the injection point is a transient field, and <tt>false</tt> otherwise
    */
   public boolean isTransient();

}

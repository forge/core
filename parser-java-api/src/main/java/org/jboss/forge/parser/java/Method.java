/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.forge.parser.java;

import java.util.List;

import org.jboss.forge.parser.Origin;

/**
 * Represents a Java Method.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface Method<O> extends Abstractable<Method<O>>, Member<O, Method<O>>, Origin<O>
{
   /**
    * Get the inner body of this {@link Method}
    */
   public String getBody();

   /**
    * Set the inner body of this {@link Method}
    */
   public Method<O> setBody(final String body);

   /**
    * Toggle this method as a constructor. If true, and the name of the {@link Method} is not the same as the name of
    * its parent {@link JavaClass} , update the name of the to match.
    */
   public Method<O> setConstructor(final boolean constructor);

   /**
    * Return true if this {@link Method} is a constructor for the class in which it is defined.
    */
   public boolean isConstructor();

   /**
    * Set the name of this {@link Method}
    */
   public Method<O> setName(final String name);

   /**
    * Get the return type of this {@link Method} or return null if the return type is void.
    */
   public String getReturnType();

   /**
    * Set this {@link Method} to return the given type.
    */
   public Method<O> setReturnType(final Class<?> type);

   /**
    * Set this {@link Method} to return the given type.
    */
   public Method<O> setReturnType(final String type);

   /**
    * Return true if this {@link Method} has a return type of 'void'
    */
   public boolean isReturnTypeVoid();

   /**
    * Set this {@link Method} to return 'void'
    */
   public Method<O> setReturnTypeVoid();

   /**
    * Set this {@link Method}'s parameters.
    */
   public Method<O> setParameters(String string);

   /**
    * Get a list of this {@link Method}'s parameters.
    */
   public List<Parameter> getParameters();

   /**
    * Convert this {@link Method} into a string representing its unique signature.
    */
   public String toSignature();

   /**
    * Add a thrown {@link Exception} to this method's signature.
    */
   public Method<O> addThrows(String type);

   /**
    * Add a thrown {@link Exception} to this method's signature.
    */
   public Method<O> addThrows(Class<? extends Exception> type);

   /**
    * Get a list of qualified (if possible) {@link Exception} class names thrown by this method.
    */
   public List<String> getThrownExceptions();

   /**
    * Remove a thrown {@link Exception} to this method's signature.
    */
   public Method<O> removeThrows(String type);

   /**
    * Remove a thrown {@link Exception} to this method's signature.
    */
   public Method<O> removeThrows(Class<? extends Exception> type);

}
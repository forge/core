/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ejb;

/**
 * @author <a href="mailto:fiorenzo.pizza@gmail.com">fiorenzo pizza</a>
 */
public enum EJBType
{
   STATELESS("javax.ejb.Stateless"),
   STATEFUL("javax.ejb.Stateful"),
   SINGLETON("javax.ejb.Singleton"),
   MESSAGEDRIVEN("javax.ejb.MessageDriven");

   private String annotation;

   private EJBType(String annotation)
   {
      this.annotation = annotation;
   }

   public String getAnnotation()
   {
      return annotation;
   }
}
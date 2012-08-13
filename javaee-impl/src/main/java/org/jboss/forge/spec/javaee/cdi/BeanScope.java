/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.cdi;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum BeanScope
{
   DEPENDENT(""),
   APPLICATION("javax.enterprise.context.ApplicationScoped"),
   SESSION("javax.enterprise.context.SessionScoped"),
   CONVERSATION("javax.enterprise.context.ConversationScoped"),
   REQUEST("javax.enterprise.context.RequestScoped"),
   CUSTOM(null);

   private String annotation;

   private BeanScope(String annotation)
   {
      this.annotation = annotation;
   }

   public String getAnnotation()
   {
      return annotation;
   }
}

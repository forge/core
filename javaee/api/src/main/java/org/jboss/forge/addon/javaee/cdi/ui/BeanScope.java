/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public enum BeanScope
{
   DEPENDENT("javax.enterprise.context.Dependent", false),
   APPLICATION("javax.enterprise.context.ApplicationScoped", true),
   SESSION("javax.enterprise.context.SessionScoped", true),
   CONVERSATION("javax.enterprise.context.ConversationScoped", true),
   REQUEST("javax.enterprise.context.RequestScoped", false),
   CUSTOM(null, false);

   private String annotation;
   private boolean serializable;

   private BeanScope(String annotation, boolean serializable)
   {
      this.annotation = annotation;
      this.serializable = serializable;
   }

   public String getAnnotation()
   {
      return annotation;
   }

   public boolean isSerializable() {
      return serializable;
   }
}

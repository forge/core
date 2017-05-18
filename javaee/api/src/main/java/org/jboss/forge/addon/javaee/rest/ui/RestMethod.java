/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.ui;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public enum RestMethod
{
   GET("doGet", javax.ws.rs.GET.class),
   POST("doPost", javax.ws.rs.POST.class),
   PUT("doPut", javax.ws.rs.PUT.class),
   DELETE("doDelete", javax.ws.rs.DELETE.class);

   private String methodName;

   private Class<? extends Annotation> methodAnnotation;

   private RestMethod(String methodName, Class<? extends Annotation> methodAnnotation)
   {
      this.methodName = methodName;
      this.methodAnnotation = methodAnnotation;
   }

   public Class<? extends Annotation> getMethodAnnotation()
   {
      return methodAnnotation;
   }

   public String getMethodName()
   {
      return methodName;
   }
}

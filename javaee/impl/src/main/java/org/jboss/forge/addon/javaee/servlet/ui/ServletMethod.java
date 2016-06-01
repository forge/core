/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet.ui;

/**
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public enum ServletMethod
{
   GET("doGet"),
   POST("doPost"),
   PUT("doPut"),
   DELETE("doDelete");

   private String methodName;

   private ServletMethod(String methodName)
   {
      this.methodName = methodName;
   }

   public String getMethodName()
   {
      return methodName;
   }
}

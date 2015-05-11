/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet.ui;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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

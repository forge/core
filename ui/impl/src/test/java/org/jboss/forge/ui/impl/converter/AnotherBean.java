/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl.converter;

public class AnotherBean
{

   private final String value;

   public AnotherBean(SimpleBean value)
   {
      this.value = value.getValue();
   }

   public AnotherBean(String value)
   {
      this.value = value;
   }

   public String getValue()
   {
      return value;
   }

   public static AnotherBean valueOf(String str)
   {
      return new AnotherBean("valueOf_" + str);
   }

}

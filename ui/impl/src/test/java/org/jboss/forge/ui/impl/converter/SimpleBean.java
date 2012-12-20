/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl.converter;

public class SimpleBean
{

   private final String value;

   public SimpleBean(String value)
   {
      this.value = value;
   }

   public SimpleBean(SimpleBean other)
   {
      this.value = other.getValue();
   }

   public String getValue()
   {
      return value;
   }

   public static SimpleBean valueOf(String str)
   {
      return new SimpleBean("valueOf_" + str);
   }

}

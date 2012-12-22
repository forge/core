/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl;

import javax.inject.Inject;

import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.impl.util.Callables;
import org.junit.Assert;
import org.junit.Test;

public class UIInputInjectionTest extends AbstractUITest
{

   @Inject
   UIInput<String> firstName;

   @Test
   public void testInjectionNotNull()
   {
      Assert.assertNotNull(firstName);
   }

   @Test
   public void testInputValues()
   {
      Assert.assertEquals("firstName", firstName.getName());
      Assert.assertEquals(String.class, firstName.getValueType());
   }

   @Test
   public void testRequired()
   {
      firstName.setRequired(true);
      Assert.assertTrue(firstName.isRequired());
      firstName.setRequired(false);
      Assert.assertFalse(firstName.isRequired());
   }

   @Test
   public void testDefaultValue()
   {
      String inputVal = "A String";
      firstName.setDefaultValue(inputVal);
      Assert.assertEquals(inputVal, firstName.getValue());
      final String inputVal2 = "Another String";

      firstName.setDefaultValue(Callables.returning(inputVal2));
      Assert.assertEquals(inputVal2, firstName.getValue());

   }
}

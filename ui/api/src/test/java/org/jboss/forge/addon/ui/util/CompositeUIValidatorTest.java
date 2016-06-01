/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.util;

import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.validate.CompositeUIValidator;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the {@link CompositeUIValidator} class
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CompositeUIValidatorTest
{

   @Test(expected = IllegalArgumentException.class)
   public void testValidate()
   {
      new CompositeUIValidator(null);
   }

   @Test
   public void testValidateMultiple()
   {
      CounterValidator c1 = new CounterValidator();
      CounterValidator c2 = new CounterValidator();
      new CompositeUIValidator(c1, c2).validate(null);
      Assert.assertEquals(1, c1.count);
      Assert.assertEquals(1, c2.count);
   }

   private static class CounterValidator implements UIValidator
   {
      int count;

      @Override
      public void validate(UIValidationContext context)
      {
         count++;
      }
   }

}

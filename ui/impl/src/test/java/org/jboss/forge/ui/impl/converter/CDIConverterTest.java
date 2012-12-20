/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl.converter;

import org.jboss.forge.ui.converter.Converter;
import org.jboss.forge.ui.impl.AbstractUITest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Injection still not working")
public class CDIConverterTest extends AbstractUITest
{

//   @Inject
   private Converter<String, Long> converter;

   @Test
   public void testNotNull() throws Exception
   {
      Assert.assertNotNull(converter);
   }

   @Test
   public void testSimpleConversion() throws Exception
   {
      String input = "123";
      Long expected = 123L;
      Assert.assertEquals(expected, converter.convert(input));
   }

}

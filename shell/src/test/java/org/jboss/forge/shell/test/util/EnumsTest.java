/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.forge.shell.test.completer.MockEnum;
import org.jboss.forge.shell.util.Enums;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class EnumsTest
{

   /**
    * Test method for {@link org.jboss.forge.shell.util.Enums#valueOf(java.lang.Class, java.lang.Object)}.
    */
   @Test
   public void testValueOf()
   {
      Enum<?> e = Enums.valueOf(MockEnum.class, "FOO");
      assertEquals(MockEnum.FOO, e);
   }

   @Test
   public void testHasValue() throws Exception
   {
      assertTrue(Enums.hasValue(MockEnum.class, "FOO"));
      assertFalse(Enums.hasValue(MockEnum.class, "WOO"));
   }

   @Test
   public void testGetValues() throws Exception
   {
      List<MockEnum> list = Enums.getValues(MockEnum.class);
      assertTrue(list.contains(MockEnum.BAR));
      assertTrue(list.contains(MockEnum.BAZ));
      assertTrue(list.contains(MockEnum.CAT));
      assertTrue(list.contains(MockEnum.DOG));
      assertTrue(list.contains(MockEnum.FOO));
      assertEquals(5, list.size());
   }

}

/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.input;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class InputComponentsTest
{
   @Inject
   private UISelectOne<String> values;

   @Inject
   private ConverterFactory converterFactory;

   @Test
   public void testUISelectOneShouldNotSetValueNotContainedInValueChoices()
   {
      values.setValueChoices(Arrays.asList("A", "B", "C"));
      InputComponents.setValueFor(converterFactory, values, "D");
      Assert.assertNull(values.getValue());
   }
}

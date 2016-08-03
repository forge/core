/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.util;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.ui.context.UIRegionBuilder;
import org.jboss.forge.addon.ui.context.UISelection;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class SelectionsTest
{
   @Test
   public void testRegionIsPresent()
   {
      List<String> list = Arrays.asList("A", "B", "C");
      UISelection<String> selection = Selections.from(r -> UIRegionBuilder.create(r).text(r), list);
      Assert.assertTrue(selection.getRegion().isPresent());
      Assert.assertEquals("A", selection.getRegion().get().getText().get());
   }
}

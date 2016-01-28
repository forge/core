/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jboss.forge.addon.ui.context.UIRegion;
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
      UISelection<String> selection = Selections.from(MockUIRegion::new, list);
      Assert.assertTrue(selection.getRegion().isPresent());
      Assert.assertEquals("A", selection.getRegion().get().getText().get());
   }

   private class MockUIRegion implements UIRegion<String>
   {
      private String resource;

      public MockUIRegion(String resource)
      {
         this.resource = resource;
      }

      @Override
      public int getStartPosition()
      {
         return 0;
      }

      @Override
      public int getEndPosition()
      {
         return 0;
      }

      @Override
      public int getStartLine()
      {
         return 0;
      }

      @Override
      public int getEndLine()
      {
         return 0;
      }

      @Override
      public Optional<String> getText()
      {
         return Optional.of(resource);
      }

      @Override
      public String getResource()
      {
         return resource;
      }
   }
}

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.util;

import java.util.Collections;
import java.util.Iterator;

import org.jboss.forge.addon.ui.context.UISelection;

/**
 * Possible {@link UISelection} implementations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public final class Selections
{
   @SuppressWarnings("unchecked")
   public static <SELECTIONTYPE> UISelection<SELECTIONTYPE> emptySelection()
   {
      return (UISelection<SELECTIONTYPE>) EmptySelection.INSTANCE;
   }

   private enum EmptySelection implements UISelection<Object>
   {
      INSTANCE;
      @Override
      public Iterator<Object> iterator()
      {
         return Collections.emptyList().iterator();
      }

      @Override
      public Object get()
      {
         return null;
      }

      @Override
      public int size()
      {
         return 0;
      }

      @Override
      public boolean isEmpty()
      {
         return true;
      }
   }
}

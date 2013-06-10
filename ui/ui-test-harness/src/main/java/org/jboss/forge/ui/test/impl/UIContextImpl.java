/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.UISelection;

public class UIContextImpl extends AbstractUIContext implements UISelection<Resource<?>>
{
   private List<Resource<?>> selection;

   public UIContextImpl(Resource<?>... initialSelection)
   {
      setInitialSelection(initialSelection);
   }

   public UIContextImpl(List<Resource<?>> initialSelection)
   {
      setInitialSelection(initialSelection);
   }

   public void setInitialSelection(Resource<?>... initialSelection)
   {
      this.selection = Arrays.asList(initialSelection);
   }

   public void setInitialSelection(List<Resource<?>> initialSelection)
   {
      this.selection = initialSelection;
   }

   @SuppressWarnings("unchecked")
   @Override
   public UISelection<Resource<?>> getInitialSelection()
   {
      return this;
   }

   @Override
   public Resource<?> get()
   {
      return selection.isEmpty() ? null : selection.get(0);
   }

   @Override
   public Iterator<Resource<?>> iterator()
   {
      return selection.iterator();
   }

   @Override
   public int size()
   {
      return selection.size();
   }

   @Override
   public boolean isEmpty()
   {
      return selection.isEmpty();
   }
}

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.result;

import org.jboss.forge.addon.ui.UICommand;

class NavigationResultImpl implements NavigationResult
{
   private final Class<? extends UICommand>[] next;

   @SuppressWarnings("unchecked")
   public NavigationResultImpl(Class<? extends UICommand>... next)
   {
      this.next = next;
   }

   @Override
   public Class<? extends UICommand>[] getNext()
   {
      return next;
   }
}

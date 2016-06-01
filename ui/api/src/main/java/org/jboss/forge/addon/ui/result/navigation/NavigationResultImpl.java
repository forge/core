/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result.navigation;

import java.util.List;

import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.NavigationResultEntry;

class NavigationResultImpl implements NavigationResult
{
   private final NavigationResultEntry[] next;

   NavigationResultImpl(NavigationResultEntry... next)
   {
      this.next = next;
   }

   NavigationResultImpl(List<NavigationResultEntry> nextList)
   {
      this.next = new NavigationResultEntry[nextList.size()];
      nextList.toArray(this.next);
   }

   @Override
   public NavigationResultEntry[] getNext()
   {
      return next;
   }
}

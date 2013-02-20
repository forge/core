/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.result;

import org.jboss.forge.ui.UICommand;

class NavigationResultImpl extends Results implements NavigationResult
{
   private Class<? extends UICommand> next;

   public NavigationResultImpl(String message, Class<? extends UICommand> next)
   {
      super(message);
      this.next = next;
   }

   @Override
   public Class<? extends UICommand> getNext()
   {
      return next;
   }

}

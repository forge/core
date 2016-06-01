/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.annotation.predicate;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.furnace.util.Predicate;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class GUIEnabledPredicate implements Predicate<UIContext>
{
   @Override
   public boolean accept(UIContext context)
   {
      return context.getProvider().isGUI();
   }
}
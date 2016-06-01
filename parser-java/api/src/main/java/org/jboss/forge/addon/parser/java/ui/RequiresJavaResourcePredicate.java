/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.furnace.util.Predicate;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RequiresJavaResourcePredicate implements Predicate<UIContext>
{
   @Override
   public boolean accept(UIContext type)
   {
      final boolean result;
      UISelection<Object> initialSelection = type.getInitialSelection();
      if (initialSelection.isEmpty())
      {
         result = false;
      }
      else
      {
         result = initialSelection.get() instanceof JavaResource;
      }
      return result;
   }
}

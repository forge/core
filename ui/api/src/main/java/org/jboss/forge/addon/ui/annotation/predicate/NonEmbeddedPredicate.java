/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.annotation.predicate;

import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Returns <code>true</code> if {@link UIProvider#isEmbedded()} returns <code>false</code>
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class NonEmbeddedPredicate implements Predicate<UIContext>
{
   /**
    * @return <code>true</code> if {@link UIProvider#isEmbedded()} returns <code>false</code>
    */
   @Override
   public boolean accept(UIContext type)
   {
      return !type.getProvider().isEmbedded();
   }

}

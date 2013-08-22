/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.mock;

import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.util.Selections;
import org.junit.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockUIContext extends AbstractUIContext
{
   private final UISelection<?> selection;

   public MockUIContext()
   {
      this(Selections.emptySelection());
   }

   public MockUIContext(UISelection<?> selection)
   {
      Assert.assertNotNull("UISelection should not be null", selection);
      this.selection = selection;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <SELECTIONTYPE> UISelection<SELECTIONTYPE> getInitialSelection()
   {
      return (UISelection<SELECTIONTYPE>) selection;
   }
}

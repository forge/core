/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.mock;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.spi.UIContextFactory;
import org.jboss.forge.ui.test.impl.UIExecutionContextImpl;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockUIContextFactory implements UIContextFactory
{

   @Override
   public UIContext createUIContext()
   {
      return new MockUIContext();
   }

   @Override
   public UIBuilder createUIBuilder(UIContext context)
   {
      return new MockUIBuilder(context);
   }

   @Override
   public UIValidationContext createUIValidationContext(UIContext context)
   {
      return new MockValidationContext(context);
   }

   @Override
   public UIExecutionContext createUIExecutionContext(UIContext context)
   {
      return new UIExecutionContextImpl(context);
   }

}

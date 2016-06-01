/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.controller.mock;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.command.UICommandTransformer;
import org.jboss.forge.addon.ui.context.UIContext;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockUICommandTransformer implements UICommandTransformer
{

   @Inject
   private ExampleNoUICommand noUICommand;

   @Override
   public UICommand transform(UIContext context, UICommand original)
   {
      return noUICommand;
   }

}

/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.annotation.handler;

import org.jboss.forge.addon.ui.context.UIContext;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class EnabledHandler implements EnableCommandHandler
{
   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }
}

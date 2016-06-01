/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.mock;

import javax.inject.Singleton;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class MockUIContextListener implements UIContextListener
{
   private boolean contextInitialized;

   @Override
   public void contextInitialized(UIContext context)
   {
      contextInitialized = true;
   }

   @Override
   public void contextDestroyed(UIContext context)
   {
      contextInitialized = false;
   }

   /**
    * @return the contextInitialized
    */
   public boolean isContextInitialized()
   {
      return contextInitialized;
   }
}

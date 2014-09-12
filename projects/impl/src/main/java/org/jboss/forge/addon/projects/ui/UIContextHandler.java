/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.ui;

import javax.inject.Singleton;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIContextListener;

/**
 * Holds an instance of a {@link UIContext}
 *
 * TODO: Move this logic to {@link DefaultFacetComponentEnricher}. Right now it is not possible to do that because the
 * following error is thrown when Ctrl+4 is pressed:
 *
 * <pre>
 * java.lang.ClassNotFoundException: javax.enterprise.inject.spi.InjectionPoint cannot be found by org.jboss.tools.forge2.runtime_2.8.1.qualifier
 * </pre>
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
class UIContextHandler implements UIContextListener
{
   private UIContext context;

   @Override
   public void contextInitialized(UIContext context)
   {
      this.context = context;
   }

   @Override
   public void contextDestroyed(UIContext context)
   {
      this.context = null;
   }

   public UIContext getContext()
   {
      return context;
   }

}

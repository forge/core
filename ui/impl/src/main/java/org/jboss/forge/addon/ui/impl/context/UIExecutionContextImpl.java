/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.context;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;

/**
 * Implementation of the {@link UIExecutionContext} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIExecutionContextImpl implements UIExecutionContext
{

   private final UIContext context;
   private final UIProgressMonitor progressMonitor;
   private final UIPrompt prompt;

   public UIExecutionContextImpl(UIContext context, UIProgressMonitor progressMonitor, UIPrompt prompt)
   {
      this.context = context;
      this.progressMonitor = progressMonitor;
      this.prompt = prompt;
   }

   @Override
   public UIContext getUIContext()
   {
      return context;
   }

   @Override
   public UIProgressMonitor getProgressMonitor()
   {
      return progressMonitor;
   }

   @Override
   public UIPrompt getPrompt()
   {
      return prompt;
   }

}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.test.impl;

import java.util.Map;

import org.jboss.forge.addon.ui.UIRuntime;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.progress.DefaultUIProgressMonitor;
import org.jboss.forge.addon.ui.progress.UIProgressMonitor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class UIRuntimeImpl implements UIRuntime
{
   private final UIProgressMonitor progressMonitor = new DefaultUIProgressMonitor();
   private final UIPrompt prompt;

   public UIRuntimeImpl(Map<String, String> promptResults)
   {
      this.prompt = new UIPromptImpl(promptResults);
   }

   @Override
   public UIProgressMonitor createProgressMonitor(UIContext context)
   {
      return progressMonitor;
   }

   @Override
   public UIPrompt createPrompt(UIContext context)
   {
      return prompt;
   }
}

/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl;

import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.input.UIPrompt;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIProviderImpl implements UIProvider
{
   private final boolean graphical;
   private final UIOutput output;
   private final UIPrompt prompt;

   public UIProviderImpl(boolean graphical)
   {
      this.graphical = graphical;
      this.output = new UIOutputImpl(System.out, System.err);
      this.prompt = new UIPromptImpl();
   }

   @Override
   public boolean isGUI()
   {
      return graphical;
   }

   @Override
   public UIOutput getOutput()
   {
      return output;
   }

   @Override
   public UIPrompt getPrompt()
   {
      return prompt;
   }

}

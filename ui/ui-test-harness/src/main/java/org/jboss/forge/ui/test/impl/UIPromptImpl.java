/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl;

import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIPromptImpl implements UIPrompt
{

   @Override
   public String prompt()
   {
      return null;
   }

   @Override
   public boolean promptBoolean(String message)
   {
      return false;
   }

}

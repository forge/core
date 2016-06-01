/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.mock;

import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockUIPrompt implements UIPrompt
{

   @Override
   public String prompt(String message)
   {
      return null;
   }

   @Override
   public String promptSecret(String message)
   {
      return null;
   }

   @Override
   public boolean promptBoolean(String message)
   {
      return true;
   }

   @Override
   public boolean promptBoolean(String message, boolean defaultValue)
   {
      return defaultValue;
   }

}

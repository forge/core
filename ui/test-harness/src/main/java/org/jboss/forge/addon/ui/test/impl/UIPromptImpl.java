/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.test.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class UIPromptImpl implements UIPrompt
{
   private final Map<String, String> promptResults;

   public UIPromptImpl(Map<String, String> promptResults)
   {
      super();
      this.promptResults = promptResults;
   }

   @Override
   public String prompt(String message)
   {
      for (Entry<String, String> entry : promptResults.entrySet())
      {
         if (message != null && message.matches(entry.getKey()))
         {
            return entry.getValue();
         }
      }
      return null;
   }

   @Override
   public String promptSecret(String message)
   {
      for (Entry<String, String> entry : promptResults.entrySet())
      {
         if (message != null && message.matches(entry.getKey()))
         {
            return entry.getValue();
         }
      }
      return null;
   }

   @Override
   public boolean promptBoolean(String message)
   {
      for (Entry<String, String> entry : promptResults.entrySet())
      {
         if (message != null && message.matches(entry.getKey()))
         {
            return Boolean.parseBoolean(entry.getValue());
         }
      }
      return true;
   }

   @Override
   public boolean promptBoolean(String message, boolean defaultValue)
   {
      for (Entry<String, String> entry : promptResults.entrySet())
      {
         if (message != null && message.matches(entry.getKey()))
         {
            return Boolean.parseBoolean(entry.getValue());
         }
      }
      return defaultValue;
   }

}

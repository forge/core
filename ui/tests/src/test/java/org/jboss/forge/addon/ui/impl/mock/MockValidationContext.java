/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.output.UIMessage;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockValidationContext implements UIValidationContext
{
   private UIContext context;
   private Map<InputComponent<?, ?>, List<String>> errors = new HashMap<InputComponent<?, ?>, List<String>>();
   private Map<InputComponent<?, ?>, List<String>> warnings = new HashMap<InputComponent<?, ?>, List<String>>();
   private Map<InputComponent<?, ?>, List<String>> infos = new HashMap<InputComponent<?, ?>, List<String>>();

   public MockValidationContext(UIContext context)
   {
      this.context = context;
   }

   @Override
   public UIContext getUIContext()
   {
      return context;
   }

   @Override
   public void addValidationError(InputComponent<?, ?> input, String errorMessage)
   {
      List<String> list = errors.get(input);
      if (list == null)
      {
         list = new ArrayList<String>();
         errors.put(input, list);
      }
      list.add(errorMessage);
   }

   @Override
   public void addValidationWarning(InputComponent<?, ?> input, String warningMessage)
   {
      List<String> list = warnings.get(input);
      if (list == null)
      {
         list = new ArrayList<String>();
         warnings.put(input, list);
      }
      list.add(warningMessage);
   }

   @Override
   public void addValidationInformation(InputComponent<?, ?> input, String infoMessage)
   {
      List<String> list = infos.get(input);
      if (list == null)
      {
         list = new ArrayList<String>();
         infos.put(input, list);
      }
      list.add(infoMessage);
   }

   @Override
   public InputComponent<?, ?> getCurrentInputComponent()
   {
      return null;
   }

   public List<String> getErrorsFor(InputComponent<?, ?> input)
   {
      return errors.get(input);
   }

   public List<String> getWarningsFor(InputComponent<?, ?> input)
   {
      return warnings.get(input);
   }

   public List<String> getInformationsFor(InputComponent<?, ?> input)
   {
      return infos.get(input);
   }

   @Override
   public List<UIMessage> getMessages()
   {
      throw new UnsupportedOperationException();
   }

}

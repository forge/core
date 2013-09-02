/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.ui.test.WizardListener;
import org.jboss.forge.ui.test.WizardTester;

/**
 * This class eases the testing of Wizards
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
@Vetoed
public class WizardTesterImpl<W extends UIWizard> implements WizardTester<W>
{
   private final AddonRegistry addonRegistry;

   private final LinkedList<UIBuilderImpl> pages = new LinkedList<UIBuilderImpl>();

   private Stack<Class<? extends UICommand>> subflows = new Stack<Class<? extends UICommand>>();

   private final UIContextImpl context;

   public WizardTesterImpl(Class<W> wizardClass, AddonRegistry addonRegistry, UIContextImpl contextImpl)
            throws Exception
   {
      this.addonRegistry = addonRegistry;
      this.context = contextImpl;
      pages.add(createBuilder(wizardClass));
   }

   @Override
   public void setInitialSelection(Resource<?>... selection)
   {
      context.setInitialSelection(selection);
   }

   @SuppressWarnings("unchecked")
   @Override
   public String next() throws Exception
   {
      if (!canFlipToNextPage())
      {
         throw new IllegalStateException("Wizard is already on the last page");
      }
      UIBuilderImpl currentBuilder = getCurrentBuilder();
      NavigationResult result = currentBuilder.getWizard().next(context);
      Class<? extends UICommand>[] successors = result.getNext();
      final Class<? extends UICommand> successor;
      if (successors == null)
      {
         successor = subflows.pop();
      }
      else
      {
         successor = successors[0];
         for (int i = 1; i < successors.length; i++)
         {
            subflows.push(successors[i]);
         }
      }
      UIBuilderImpl nextBuilder = createBuilder((Class<W>) successor);
      pages.add(nextBuilder);
      return result.getMessage();
   }

   @Override
   public void previous() throws Exception
   {
      if (!canFlipToPreviousPage())
      {
         throw new IllegalStateException("Wizard is already on the first page");
      }
      pages.removeLast();
   }

   @Override
   public boolean canFlipToNextPage()
   {
      UIBuilderImpl currentBuilder = getCurrentBuilder();
      try
      {
         boolean result;
         NavigationResult next = currentBuilder.getWizard().next(context);
         if (next == null)
         {
            result = !subflows.isEmpty();
         }
         else
         {
            result = true;
         }
         return result;
      }
      catch (Exception e)
      {
         throw new IllegalStateException(e);
      }
   }

   @Override
   public boolean canFlipToPreviousPage()
   {
      return pages.size() > 1;
   }

   @Override
   public boolean canFinish()
   {
      return getValidationErrors().isEmpty() && !canFlipToNextPage();
   }

   @Override
   public boolean isValid()
   {
      return getValidationErrors().isEmpty();
   }

   @Override
   public List<String> getValidationErrors()
   {
      return getValidationErrors(getCurrentBuilder());
   }

   @Override
   public void finish(WizardListener listener) throws Exception
   {
      for (UIBuilderImpl builder : pages)
      {
         // validate before execute
         List<String> errors = getValidationErrors(builder);
         if (!errors.isEmpty())
         {
            throw new IllegalStateException(errors.toString());
         }
      }
      // All good. Hit it !
      for (UIBuilderImpl builder : pages)
      {
         UIWizard wizard = builder.getWizard();
         Result result = wizard.execute(context);
         if (listener != null)
         {
            listener.wizardExecuted(wizard, result);
         }
      }
   }

   private UIBuilderImpl getCurrentBuilder()
   {
      return pages.peekLast();
   }

   private UIBuilderImpl createBuilder(Class<W> wizardClass) throws Exception
   {
      W wizard = addonRegistry.getServices(wizardClass).get();
      UIBuilderImpl builder = new UIBuilderImpl(context, wizard);
      wizard.initializeUI(builder);
      return builder;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void setValueFor(String property, Object value)
   {
      UIBuilderImpl currentBuilder = getCurrentBuilder();
      InputComponent<?, ?> input = currentBuilder.getComponentNamed(property);
      if (input == null)
      {
         throw new IllegalArgumentException("Property " + property + " not found for current wizard page");
      }
      InputComponents.setValueFor(getConverterFactory(), (InputComponent<?, Object>) input, value);
   }

   private List<String> getValidationErrors(UIBuilderImpl builder)
   {
      UIWizard currentWizard = builder.getWizard();
      UIValidationContextImpl validationContext = new UIValidationContextImpl(context);
      currentWizard.validate(validationContext);
      return validationContext.getErrors();
   }

   private ConverterFactory getConverterFactory()
   {
      return addonRegistry.getServices(ConverterFactory.class).get();
   }
}

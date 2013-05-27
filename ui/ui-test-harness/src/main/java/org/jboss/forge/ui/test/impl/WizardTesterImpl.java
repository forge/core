/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl;

import java.util.LinkedList;
import java.util.List;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.ui.test.WizardListener;
import org.jboss.forge.ui.test.WizardState;
import org.jboss.forge.ui.test.WizardTester;

/**
 * This class eases the testing of Wizards
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class WizardTesterImpl<W extends UIWizard> implements WizardTester<W>
{
   private final AddonRegistry addonRegistry;

   private final LinkedList<UIBuilderImpl> pages = new LinkedList<UIBuilderImpl>();

   private final UIContextImpl context;

   private WizardState state = WizardState.FIRST;

   WizardTesterImpl(Class<W> wizardClass, AddonRegistry addonRegistry, UIContextImpl contextImpl)
            throws Exception
   {
      this.addonRegistry = addonRegistry;
      this.context = contextImpl;
      pages.add(createBuilder(wizardClass));
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
      UIBuilderImpl nextBuilder = createBuilder((Class<W>) result.getNext());
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
         return currentBuilder.getWizard().next(context) != null;
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

   @Override
   public WizardState getCurrentState()
   {
      return state;
   }

   private UIBuilderImpl getCurrentBuilder()
   {
      return pages.peekLast();
   }

   private UIBuilderImpl createBuilder(Class<W> wizardClass) throws Exception
   {
      W wizard = addonRegistry.getExportedInstance(wizardClass).get();
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
      return addonRegistry.getExportedInstance(ConverterFactory.class).get();
   }
}

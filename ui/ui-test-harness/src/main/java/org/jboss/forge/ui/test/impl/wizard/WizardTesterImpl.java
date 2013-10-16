/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl.wizard;

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
import org.jboss.forge.ui.test.impl.UIBuilderImpl;
import org.jboss.forge.ui.test.impl.UIContextImpl;
import org.jboss.forge.ui.test.impl.UIValidationContextImpl;

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

   private final Class<W> wizardClass;

   public WizardTesterImpl(Class<W> wizardClass, AddonRegistry addonRegistry, UIContextImpl contextImpl)
   {
      this.addonRegistry = addonRegistry;
      this.context = contextImpl;
      this.wizardClass = wizardClass;
   }

   @Override
   public void setInitialSelection(Resource<?>... selection)
   {
      context.setInitialSelection(selection);
   }

   @Override
   public void launch() throws Exception
   {
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
      NavigationResult result = ((UIWizard) currentBuilder.getCommand()).next(context);
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
         NavigationResult next = ((UIWizard) currentBuilder.getCommand()).next(context);
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
      try
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
            UICommand wizard = builder.getCommand();
            Result result = wizard.execute(context);
            if (listener != null)
            {
               listener.wizardExecuted((UIWizard) wizard, result);
            }
         }
      }
      finally
      {
         context.destroy();
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

   @Override
   public InputComponent<?, ?> getInputComponent(String property)
   {
      UIBuilderImpl currentBuilder = getCurrentBuilder();
      return currentBuilder.getComponentNamed(property);
   }

   private List<String> getValidationErrors(UIBuilderImpl builder)
   {
      UICommand currentWizard = builder.getCommand();
      UIValidationContextImpl validationContext = new UIValidationContextImpl(context);

      for (InputComponent<?, ?> input : builder.getInputs())
      {
         input.validate(validationContext);
      }

      currentWizard.validate(validationContext);
      return validationContext.getErrors();
   }

   private ConverterFactory getConverterFactory()
   {
      return addonRegistry.getServices(ConverterFactory.class).get();
   }

   @Override
   public boolean isEnabled()
   {
      return getCurrentBuilder().getCommand().isEnabled(context);
   }
}

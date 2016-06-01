/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards;

import java.util.Arrays;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;

public class ExampleWizard extends AbstractUICommand implements UIWizard
{

   @Inject
   @WithAttributes(label = "First Name", shortName = 'f', required = true)
   private UIInput<String> firstName;

   @Inject
   @WithAttributes(label = "Show Select Components", shortName = 's')
   private UISelectOne<Boolean> showSelectComponents;

   @Inject
   @WithAttributes(label = "One Career", shortName = 'o')
   private UISelectOne<Career> career;

   @Inject
   @WithAttributes(label = "Many Career", shortName = 'm')
   private UISelectMany<Career> manyCareer;

   @Inject
   @WithAttributes(label = "Folder Location:", shortName = 'd')
   private UIInput<DirectoryResource> directory;

   @Inject
   @WithAttributes(label = "Go to Last Step", shortName = 'g')
   private UIInput<Boolean> goToLastStep;

   @Inject
   private UISelectOne<String> valueWithSpaces;

   @Inject
   private UIInputMany<String> manyValues;

   @Inject
   private UISelectMany<String> selectManyValues;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Wizard").description("This is the First screen of the Wizard");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      firstName.setRequired(true).setRequiredMessage("First Name must be informed !").setNote("This is a UIInput");
      valueWithSpaces.setValueChoices(Arrays.asList("Value 1", "Value 2", "Value 10", "Value 100")).setNote(
               "This is a UISelectOne");
      selectManyValues.setValueChoices(Arrays.asList("A", "B", "C", "AA", "BB")).setNote("This is a UISelectMany");
      manyValues.setNote("This is a UIInputMany");
      directory.setNote(new Callable<String>()
      {
         @Override
         public String call() throws Exception
         {
            DirectoryResource value = directory.getValue();
            return value == null ? null : "Path: " + value.getFullyQualifiedName();
         }
      });
      builder.add(firstName).add(showSelectComponents).add(goToLastStep).add(directory).add(valueWithSpaces)
               .add(career).add(manyCareer).add(manyValues).add(selectManyValues);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      String value = firstName.getValue();
      if (value != null && !value.isEmpty() && !value.matches("[a-zA-Z]+"))
      {
         context.addValidationError(firstName, "First Name contains invalid characters");
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success();
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      if (showSelectComponents.getValue() != null && showSelectComponents.getValue())
      {
         return Results.navigateTo(ExampleSelectComponents.class);
      }
      if (goToLastStep.getValue() != null && goToLastStep.getValue())
      {
         return Results.navigateTo(ExampleStepTwo.class);
      }
      return Results.navigateTo(ExampleStepOne.class);
   }
}

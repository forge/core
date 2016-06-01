/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards.subflow;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ExampleFlow extends AbstractUICommand implements UIWizard
{
   @Inject
   @WithAttributes(label = "Name", required = true)
   private UIInput<String> name;

   @Inject
   @WithAttributes(label = "Number", required = true)
   private UIInput<Integer> number;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(name).add(number);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success();
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      validator.addValidationInformation(name, "Input the name and click that next button");
      if ("foo".equals(name.getValue()))
      {
         validator.addValidationWarning(name, "Foo? Really? Ok...");
      }
      if (number.getValue() != null && number.getValue().intValue() != 42)
      {
         validator.addValidationError(number,
                  "You can't proceed without knowing the secret of life,the universe and everything!");
      }
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("flow").category(Categories.create("Example"));
   }

   @SuppressWarnings("unchecked")
   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return Results.navigateTo(FlowOneStep.class, FlowTwoStep.class);
   }

}

/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.command;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.validate.UIValidator;
import org.jboss.forge.furnace.util.Assert;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ValidateRequiredCommand extends AbstractUICommand
{

   @Inject
   @WithAttributes(label = "Input", required = true)
   private UIInput<String> input;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      input.addValidator(new UIValidator()
      {
         @Override
         public void validate(UIValidationContext context)
         {
            Assert.notNull(input.getValue(), "Value should not be null inside input validator because it is required");      
         }
      });
      builder.add(input);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      Assert.notNull(input.getValue(), "Value should not be null because it is required");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success(input.getValue());
   }

}

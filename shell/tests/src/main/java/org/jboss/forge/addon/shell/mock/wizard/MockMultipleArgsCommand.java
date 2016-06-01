/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.mock.wizard;

import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Lists;

/**
 * @author Vineet Reynolds
 * 
 */
public class MockMultipleArgsCommand extends AbstractUICommand implements UICommand
{

   @Inject
   @WithAttributes(label = "values")
   private UIInputMany<String> values;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("mock-command-inputmany")
               .description("Mock command with input-many argument");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(values);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      StringBuilder inputValues = new StringBuilder();
      for (String value : values.getValue())
      {
         inputValues.append(value);
         inputValues.append(' ');
      }
      return Results.success("Command executed with input values : " + inputValues.toString());
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      List<String> requiredValue = Lists.toList(values.getValue());
      if (requiredValue.size() < 1)
         validator.addValidationError(values, "Must specify at least one value for --values before continuing.");
   }

}

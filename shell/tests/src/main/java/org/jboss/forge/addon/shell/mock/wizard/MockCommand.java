/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.mock.wizard;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockCommand extends AbstractUICommand implements UICommand
{
   @Inject
   private UIInput<Boolean> key;

   @Inject
   @WithAttributes(defaultValue = "false", label = "proceed")
   private UIInput<Boolean> proceed;

   @Inject
   @SuppressWarnings("rawtypes")
   private UIInputMany<Resource> values;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("mock-command").description("Mock it up - Command style");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(key).add(values).add(proceed);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success("Begin step executed.");
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      if (!proceed.getValue())
         validator.addValidationError(proceed, "Must --proceed before continuing.");
   }

}

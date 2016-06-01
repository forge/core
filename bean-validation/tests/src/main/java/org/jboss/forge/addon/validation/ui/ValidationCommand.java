/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.validation.ui;

import javax.inject.Inject;
import javax.validation.constraints.Size;

import org.hibernate.validator.valuehandling.UnwrapValidatedValue;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ValidationCommand extends AbstractUICommand
{
   @Inject
   @WithAttributes(label = "Name", required = true)
   @Size(min = 1, max = 5)
   @UnwrapValidatedValue
   private UIInput<String> name;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(name);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success();
   }

}

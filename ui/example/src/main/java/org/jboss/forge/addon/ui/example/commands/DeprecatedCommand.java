/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.commands;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class DeprecatedCommand extends AbstractUICommand
{

   @Inject
   @Deprecated
   private UIInput<String> deprecatedInput;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(deprecatedInput);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("deprecated-command").deprecated(true)
               .deprecatedMessage("Do not use this command anymore");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results
               .success("Command 'deprecated-command' successfully executed with input " + deprecatedInput.getValue());
   }

}

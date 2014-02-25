/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller.mock;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MockPreStepsCommand extends AbstractUICommand implements PrerequisiteCommandsProvider
{
   @Inject
   @WithAttributes(label = "Name", required = true)
   UIInput<String> name;

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

   @Override
   public Collection<Class<? extends UICommand>> getPrerequisiteCommands(UIContext context)
   {
      return Collections.<Class<? extends UICommand>> singleton(ExampleCommand.class);
   }

}

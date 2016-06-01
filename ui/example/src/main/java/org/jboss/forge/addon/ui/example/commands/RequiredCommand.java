/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.commands;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class RequiredCommand extends AbstractUICommand
{

   @Inject
   @WithAttributes(label = "Name", required = true)
   UIInput<String> name;

   @Inject
   @WithAttributes(label = "Names", required = true)
   UIInputMany<String> names;

   @Inject
   @WithAttributes(label = "Names Select One", required = true)
   UISelectOne<String> nameSelectOne;

   @Inject
   @WithAttributes(label = "Names Select Many", required = true)
   UISelectMany<String> nameSelectMany;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      List<String> optionValues = Arrays.asList("APPLE", "ORANGE", "PINEAPPLE");
      nameSelectOne.setValueChoices(optionValues);
      nameSelectMany.setValueChoices(optionValues);
      builder.add(name).add(names).add(nameSelectOne).add(nameSelectMany);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      StringBuilder result = new StringBuilder();
      result.append("Name = ").append(name.getValue()).append("\n");
      result.append("Names = ").append(names.getValue()).append("\n");
      result.append("Names Select One = ").append(nameSelectOne.getValue()).append("\n");
      result.append("Names Select Many = ").append(nameSelectMany.getValue()).append("\n");

      return Results.success(result.toString());
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("required-command");
   }

}

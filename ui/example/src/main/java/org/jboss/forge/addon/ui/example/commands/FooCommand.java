/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.commands;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class FooCommand implements UICommand
{
   @Inject
   @WithAttributes(label = "Name the foo", required = true, defaultValue = "BAR")
   private UIInput<String> name;

   @Inject
   @WithAttributes(name = "Help Me", label = "help")
   private UIInput<String> help;

   @Inject
   @WithAttributes(label = "bool")
   private UIInput<Boolean> bool;

   @Inject
   @WithAttributes(label = "bar", required = true, defaultValue = "BAAAR")
   private UIInput<String> bar;

   @Inject
   @WithAttributes(label = "bar2", type = InputType.TEXTAREA)
   private UIInput<String> bar2;

   @Inject
   @WithAttributes(label = "target location")
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   private UISelectOne<String> valueWithSpaces;

   @Inject
   @WithAttributes(label = "One Career", shortName = 'o')
   private UISelectOne<Career> career;

   @Inject
   @WithAttributes(label = "Many Career", shortName = 'm')
   private UISelectMany<Career> manyCareer;

   @Inject
   @WithAttributes(label = "Disabled", enabled = false)
   private UIInput<String> disabledOption;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("foocommand").description("Do some foo");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      valueWithSpaces.setValueChoices(Arrays.asList("Value 1", "Value 2", "Value 10", "Value 100"));
      help.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
         {
            if (value == null || value.length() == 0)
            {
               ArrayList<String> list = new ArrayList<String>();
               list.add("HELP");
               list.add("HALP");
               return list;
            }
            return null;
         }
      });

      builder.add(name).add(help).add(bool).add(bar).add(bar2).add(targetLocation).add(valueWithSpaces).add(career)
               .add(manyCareer).add(disabledOption);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success("Help: " + help.getValue());
   }
}

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.mock.command;

import java.util.ArrayList;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.services.Exported;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Exported
public class FooCommand implements UICommand
{
   @Inject
   @WithAttributes(label = "Name the foo", required = true, defaultValue = "BAR")
   private UIInput<String> name;

   @Inject
   @WithAttributes(label = "help")
   private UIInput<String> help;

   @Inject
   @WithAttributes(label = "bool")
   private UIInput<Boolean> bool;

   @Inject
   @WithAttributes(label = "bar", required = true, defaultValue = "BAAAR")
   private UIInput<String> bar;

   @Inject
   @WithAttributes(label = "bar2")
   private UIInput<String> bar2;

   @Inject
   @WithAttributes(label = "target location")
   private UIInput<DirectoryResource> targetLocation;

   @Override
   public UICommandMetadata getMetadata()
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

      builder.add(name).add(help).add(bool).add(bar).add(bar2).add(targetLocation);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      return Results.success("boo");
   }
}

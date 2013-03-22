/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.input.UICompleter;
import org.jboss.forge.ui.input.UIInput;
import org.jboss.forge.ui.input.InputComponent;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Exported
public class FooCommand implements UICommand
{
   @Inject
   private UIInput<String> name;

   @Inject
   private UIInput<String> foo;

   @Inject
   private UIInput<Boolean> bool;

   @Inject
   private UIInput<String> bar;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("foo").description("Do some foo");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      name.setLabel("foo");
      name.setRequired(true);

      foo.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(InputComponent<?, String> input, String value)
         {
            List<String> out = new ArrayList<String>();
            out.add("foo1");
            return out;
         }
      });

      builder.add(name).add(foo).add(bool).add(bar);
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

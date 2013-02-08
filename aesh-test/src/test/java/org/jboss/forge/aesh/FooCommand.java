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
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.Results;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.UICommandMetadata;
import org.jboss.forge.ui.UICompleter;
import org.jboss.forge.ui.UIContext;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.UIInputComponent;
import org.jboss.forge.ui.UIValidationContext;
import org.jboss.forge.ui.base.UICommandMetadataBase;

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
      return new UICommandMetadataBase("foo", "Do some foo");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIContext context) throws Exception
   {
      name.setLabel("foo");
      name.setRequired(true);

       foo.setCompleter(new UICompleter<String>() {
           @Override
           public Iterable<String> getCompletionProposals(UIInputComponent<?,String> input, String value) {
               List<String> out = new ArrayList<String>();
               out.add("foo1");
               return out;
           }
       });

       context.getUIBuilder().add(name);
       context.getUIBuilder().add(foo);
       context.getUIBuilder().add(bool);
       context.getUIBuilder().add(bar);
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

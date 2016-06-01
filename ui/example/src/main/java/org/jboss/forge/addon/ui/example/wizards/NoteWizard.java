/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class NoteWizard extends AbstractUICommand
{

   @Inject
   @WithAttributes(label = "First Name")
   private UIInput<String> firstName;
   @Inject
   @WithAttributes(label = "Last Name")
   private UIInput<String> lastName;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      firstName.setNote(new Callable<String>()
      {
         @Override
         public String call() throws Exception
         {
            return firstName.getValue() + lastName.getValue();
         }
      });
      builder.add(firstName).add(lastName);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success("Hello, " + firstName.getValue());
   }

}

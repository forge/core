/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MockWizardStep extends AbstractUICommand implements UIWizardStep
{
   @Inject
   @WithAttributes(defaultValue = "false", label = "done")
   private UISelectOne<Boolean> done;

   @Inject
   private UISelectMany<String> selections;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(selections).add(done);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success("Begin step executed.");
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      if (!done.getValue())
         validator.addValidationError(done, "Must be --done before continuing.");
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }

}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards.exception;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ExceptionWizard extends AbstractUICommand implements UIWizard
{

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      throw new Exception("Exception on Initialize UI!");
   }

   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(ExceptionWizard.class).name("exception-test")
               .description("Tests Exceptions");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      throw new Exception("Exception on Result!");
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      throw new Exception("Exception on Next!");
   }

}

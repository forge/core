/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.command;

import javax.enterprise.inject.Vetoed;
import javax.inject.Singleton;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.command.PrerequisiteCommandsProvider;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.command.UICommandTransformer;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * Adds the {@link NavigationResult} steps provided by
 * {@link PrerequisiteCommandsProvider#getPrerequisiteCommands(UIContext)} as the first steps to be executed only if the
 * requested {@link UICommand} implements the {@link PrerequisiteCommandsProvider} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class PrerequisiteCommandTransformer implements UICommandTransformer
{
   @Override
   public UICommand transform(UIContext context, UICommand original)
   {
      final UICommand result;
      if (original instanceof PrerequisiteCommandsProvider)
      {
         NavigationResult navigationResult = ((PrerequisiteCommandsProvider) original).getPrerequisiteCommands(context);
         if (navigationResult == null || navigationResult.getNext().length == 0)
         {
            result = original;
         }
         else
         {
            NavigationResultBuilder builder = NavigationResultBuilder.create(navigationResult);
            builder.add(original);
            result = new DelegateWizard(original, builder.build());
         }
      }
      else
      {
         result = original;
      }
      return result;
   }

   @Vetoed
   private static class DelegateWizard extends AbstractUICommand implements UIWizard
   {
      private final UICommand originalCmd;
      private final NavigationResult result;

      DelegateWizard(UICommand originalCmd, NavigationResult result)
      {
         this.originalCmd = originalCmd;
         this.result = result;
      }

      @Override
      public UICommandMetadata getMetadata(UIContext context)
      {
         return originalCmd.getMetadata(context);
      }

      @Override
      public NavigationResult next(UINavigationContext context) throws Exception
      {
         return result;
      }

      @Override
      public Result execute(UIExecutionContext context) throws Exception
      {
         return Results.success();
      }

      @Override
      public void initializeUI(UIBuilder builder) throws Exception
      {
         // no UI
      }
   }
}
/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * Adds the Setup steps on {@link AbstractJavaEECommand}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Singleton
public class PrerequisiteCommandTransformer implements UICommandTransformer
{
   @SuppressWarnings("unchecked")
   @Override
   public UICommand transform(UIContext context, UICommand original)
   {
      final UICommand result;
      if (original instanceof PrerequisiteCommandsProvider)
      {
         Collection<Class<? extends UICommand>> previousSteps = toCollection(((PrerequisiteCommandsProvider) original)
                  .getPrerequisiteCommands(context));
         if (previousSteps == null || previousSteps.isEmpty())
         {
            result = original;
         }
         else
         {
            Class<? extends UICommand> classes[] = new Class[previousSteps.size() + 1];
            previousSteps.toArray(classes);
            classes[classes.length - 1] = (Class<? extends UICommand>) original.getMetadata(context).getType();
            NavigationResult navigateTo = Results.navigateTo(classes);
            result = new DelegateWizard(original, navigateTo);
         }
      }
      else
      {
         result = original;
      }
      return result;
   }

   private <T> Collection<T> toCollection(Iterable<T> iterable)
   {
      if (iterable == null)
      {
         return null;
      }
      else if (iterable instanceof Collection)
      {
         return (Collection<T>) iterable;
      }
      else
      {
         List<T> list = new ArrayList<>();
         for (T obj : iterable)
         {
            list.add(obj);
         }
         return list;
      }
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
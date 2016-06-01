/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result.navigation;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * Delegates to multiple {@link UICommand} object
 * 
 * NOTE: {@link UIWizard} implementations are NOT navigable in this command, that is,
 * {@link UIWizard#next(org.jboss.forge.addon.ui.context.UINavigationContext)} will never be called in the delegated
 * commands.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Vetoed
class CompositeCommand implements UICommand
{
   private final UICommandMetadata metadata;
   private final Iterable<UICommand> commands;

   public CompositeCommand(UICommandMetadata metadata, Iterable<UICommand> commands)
   {
      this.metadata = metadata;
      this.commands = commands;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return metadata;
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      for (UICommand command : commands)
      {
         command.initializeUI(builder);
      }
   }

   @Override
   public void validate(UIValidationContext context)
   {
      for (UICommand command : commands)
      {
         command.validate(context);
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      List<Result> results = new LinkedList<>();
      for (UICommand command : commands)
      {
         Result result = command.execute(context);
         results.add(result);
      }
      return Results.aggregate(results);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((commands == null) ? 0 : commands.hashCode());
      result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      CompositeCommand other = (CompositeCommand) obj;
      if (commands == null)
      {
         if (other.commands != null)
            return false;
      }
      else if (!commands.equals(other.commands))
         return false;
      if (metadata == null)
      {
         if (other.metadata != null)
            return false;
      }
      else if (!metadata.equals(other.metadata))
         return false;
      return true;
   }

}

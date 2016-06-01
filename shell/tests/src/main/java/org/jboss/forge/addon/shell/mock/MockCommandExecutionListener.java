/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.mock;

import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.util.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MockCommandExecutionListener extends AbstractCommandExecutionListener
{
   private boolean pre;
   private boolean post;
   private Result result;

   @Override
   public void preCommandExecuted(UICommand command, UIExecutionContext context)
   {
      Assert.notNull(command, "Command must not be null.");
      Assert.notNull(context, "Context must not be null.");
      this.pre = true;
   }

   @Override
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
   {
      Assert.notNull(command, "Command must not be null.");
      Assert.notNull(context, "Context must not be null.");
      Assert.notNull(result, "Result must not be null.");
      this.post = true;
      this.result = result;
   }

   public boolean isPreExecuted()
   {
      return pre;
   }

   public boolean isPostExecuted()
   {
      return post;
   }

   public Result getResult()
   {
      return result;
   }
}

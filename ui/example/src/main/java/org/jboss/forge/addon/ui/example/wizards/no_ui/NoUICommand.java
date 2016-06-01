/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards.no_ui;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class NoUICommand extends AbstractUICommand
{

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      // no inputs added to UIBuilder
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success("Executed");
   }

}

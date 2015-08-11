/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script.ui;

<<<<<<< HEAD
import org.jboss.forge.addon.ui.annotation.Command;
=======
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;
>>>>>>> 72e8c873ad9b11f291f20c2ef5205d009904579a

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
<<<<<<< HEAD
public class ThrowExceptionCommand
{
   @Command("throw-exception")
   public String throwIt()
=======
public class ThrowExceptionCommand extends AbstractUICommand
{
   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("throw-it");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
>>>>>>> 72e8c873ad9b11f291f20c2ef5205d009904579a
   {
      throw new UnsupportedOperationException("Intentional failure.");
   }
}

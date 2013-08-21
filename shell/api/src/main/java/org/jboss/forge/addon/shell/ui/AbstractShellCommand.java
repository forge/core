/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Base class for Shell-only commands
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractShellCommand extends AbstractUICommand
{
   @Override
   public boolean isEnabled(UIContext context)
   {
      return context instanceof ShellContext;
   }

   @Override
   public Metadata getMetadata()
   {
      return super.getMetadata().category(Categories.create("Shell"));
   }

   @Override
   public final Result execute(UIContext context) throws Exception
   {
      return execute((ShellContext) context);
   }

   public abstract Result execute(ShellContext shellContext) throws Exception;
}

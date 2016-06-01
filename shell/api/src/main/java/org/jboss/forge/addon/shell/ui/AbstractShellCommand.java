/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.ui;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
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
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).category(Categories.create("Shell"));
   }

   @Override
   public final boolean isEnabled(UIContext context)
   {
      return (context instanceof ShellContext) && isEnabled((ShellContext) context);
   }

   // This method is meant to be overridden
   public boolean isEnabled(ShellContext context)
   {
      return true;
   }
}

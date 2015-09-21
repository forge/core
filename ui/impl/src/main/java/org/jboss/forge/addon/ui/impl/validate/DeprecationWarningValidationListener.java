/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.impl.validate;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.validate.UIValidationListener;
import org.jboss.forge.furnace.util.Strings;

/**
 * Adds a deprecation warning to a given command
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class DeprecationWarningValidationListener implements UIValidationListener
{
   @Override
   public void preValidate(UIValidationContext context, UICommand command)
   {
      // Do nothing
   }

   @Override
   public void postValidate(UIValidationContext context, UICommand command)
   {
      UICommandMetadata metadata = command.getMetadata(context.getUIContext());
      if (metadata.isDeprecated())
      {
         String msg = String.format(
                  "The command '%s' is deprecated and will be removed in future versions.", metadata.getName());
         if (!Strings.isNullOrEmpty(metadata.getDeprecatedMessage()))
         {
            msg += " " + metadata.getDeprecatedMessage();
         }
         context.addValidationWarning(null, msg);
      }
   }

}

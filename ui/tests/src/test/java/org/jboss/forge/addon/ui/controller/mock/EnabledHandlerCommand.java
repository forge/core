/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller.mock;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.handler.DisabledPredicate;
import org.jboss.forge.addon.ui.annotation.handler.EnabledPredicate;
import org.jboss.forge.addon.ui.annotation.handler.GUIEnabledPredicate;
import org.jboss.forge.addon.ui.annotation.handler.NonGUIEnabledPredicate;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class EnabledHandlerCommand
{

   @Command(enabled = EnabledPredicate.class)
   public void enabled(final UIOutput output)
   {
      output.out().println("enabled executed!");
   }

   @Command(enabled = DisabledPredicate.class)
   public void disabled(final UIOutput output)
   {
      output.out().println("disabled executed!");
   }

   @Command(enabled = NonGUIEnabledPredicate.class)
   public void nongui(final UIOutput output)
   {
      output.out().println("nongui executed!");
   }

   @Command(enabled = GUIEnabledPredicate.class)
   public void gui(final UIOutput output)
   {
      output.out().println("gui executed!");
   }

}

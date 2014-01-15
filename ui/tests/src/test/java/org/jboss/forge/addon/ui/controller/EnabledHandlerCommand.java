/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.handler.DisabledHandler;
import org.jboss.forge.addon.ui.annotation.handler.EnabledHandler;
import org.jboss.forge.addon.ui.annotation.handler.GUIEnabledHandler;
import org.jboss.forge.addon.ui.annotation.handler.NonGUIEnabledHandler;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class EnabledHandlerCommand
{

   @Command(enabledHandler = EnabledHandler.class)
   public void enabled(final UIOutput output)
   {
      output.out().println("enabled executed!");
   }

   @Command(enabledHandler = DisabledHandler.class)
   public void disabled(final UIOutput output)
   {
      output.out().println("disabled executed!");
   }

   @Command(enabledHandler = NonGUIEnabledHandler.class)
   public void nongui(final UIOutput output)
   {
      output.out().println("nongui executed!");
   }

   @Command(enabledHandler = GUIEnabledHandler.class)
   public void gui(final UIOutput output)
   {
      output.out().println("gui executed!");
   }

}

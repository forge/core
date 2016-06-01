/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.predicate.NonGUIEnabledPredicate;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.furnace.versions.Versions;

/**
 * The "About" command
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class AboutCommand
{
   @Command(value = "version", help = "Displays the current Forge version.", enabled = NonGUIEnabledPredicate.class)
   public void showVersion(final UIOutput output)
   {
      Version version = Versions.getImplementationVersionFor(getClass());
      output.out().println(
               "JBoss Forge, version [ " + version + " ] - JBoss, by Red Hat, Inc. [ http://jboss.org/forge ]");
   }

   @Command(value = "about", help = "Display information about this forge.", enabled = NonGUIEnabledPredicate.class)
   public void showAbout(final UIOutput output)
   {
      output.out().println();
      output.out().println("    _____                    ");
      output.out().println("   |  ___|__  _ __ __ _  ___ ");
      output.out().println("   | |_ / _ \\| `__/ _` |/ _ \\  \\\\");
      output.out().println("   |  _| (_) | | | (_| |  __/  //");
      output.out().println("   |_|  \\___/|_|  \\__, |\\___| ");
      output.out().println("                   |___/      ");
      output.out().println("");
      showVersion(output);
   }
}

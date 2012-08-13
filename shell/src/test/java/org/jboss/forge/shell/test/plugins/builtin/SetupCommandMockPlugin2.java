/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;

/**
 * @author <a href="mailto:sachsedaniel@gmail.com">Daniel 'Wombat' Sachse</a>
 * 
 */
@Alias("testplugin2")
@RequiresFacet({ MockFacet.class })
public class SetupCommandMockPlugin2 implements Plugin
{
   @Inject
   private Event<InstallFacets> install;

   @SetupCommand
   public void setup()
   {
      install.fire(new InstallFacets(MockFacet2.class));
   }

   @Command
   public void other()
   {

   }

   @DefaultCommand
   public void deflt()
   {
   }
}
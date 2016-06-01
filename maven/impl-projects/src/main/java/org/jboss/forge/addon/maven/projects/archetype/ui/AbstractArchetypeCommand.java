/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype.ui;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.maven.archetype.ArchetypeCatalogFactoryRegistry;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractArchetypeCommand extends AbstractUICommand
{
   protected Configuration getConfiguration()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), Configuration.class).get()
               .subset("maven.archetypes");
   }

   protected ArchetypeCatalogFactoryRegistry getArchetypeCatalogFactoryRegistry()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), ArchetypeCatalogFactoryRegistry.class).get();
   }
}

/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.extension;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.jboss.forge.addon.ui.cdi.CommandScoped;
import org.jboss.forge.addon.ui.impl.CommandScopedContext;

/**
 * An extension to provide {@link CommandScoped} support.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class CommandScopedExtension implements Extension
{
   public void registerContext(@Observes final AfterBeanDiscovery event)
   {
      event.addContext(new CommandScopedContext());
   }
}
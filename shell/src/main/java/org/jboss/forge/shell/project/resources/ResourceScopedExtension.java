/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project.resources;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import org.jboss.forge.shell.project.ResourceScoped;

/**
 * An extension to provide {@link ResourceScoped} support.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ResourceScopedExtension implements Extension
{

   public void registerContext(@Observes final AfterBeanDiscovery event, final BeanManager manager)
   {
      event.addContext(new ResourceScopedContext(manager));
   }

}

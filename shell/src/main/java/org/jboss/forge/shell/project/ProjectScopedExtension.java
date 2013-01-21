/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.project;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * An extension to provide {@link ProjectScoped} support.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ProjectScopedExtension implements Extension
{

   public void registerContext(@Observes final AfterBeanDiscovery event, final BeanManager manager)
   {
      event.addContext(new ProjectScopedContext(manager));
   }

}

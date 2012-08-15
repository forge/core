/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;

import org.jboss.forge.container.events.PreStartup;

@Singleton
public class ObserverBeanManagerProvider
{
   private BeanManager manager;

   public void grab(@Observes final PreStartup event, final BeanManager m)
   {
      manager = m;
   }

   public BeanManager getBeanManager()
   {
      return manager;
   }

}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;

import org.jboss.forge.shell.events.PreStartup;
import org.jboss.solder.beanManager.BeanManagerProvider;

@Singleton
public class ObserverBeanManagerProvider implements BeanManagerProvider
{
   private BeanManager manager;

   public void grab(@Observes final PreStartup event, final BeanManager m)
   {
      manager = m;
   }

   @Override
   public BeanManager getBeanManager()
   {
      return manager;
   }

   @Override
   public int getPrecedence()
   {
      return 12;
   }

}

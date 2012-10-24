package org.jboss.forge.container.impl;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class BeanRegistrant implements Extension
{
   public void registerWeldSEBeans(@Observes BeforeBeanDiscovery event, BeanManager manager)
   {
      event.addAnnotatedType(manager.createAnnotatedType(AddonRegistry.class));
      event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryImpl.class));
      event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryInitializer.class));
   }
}

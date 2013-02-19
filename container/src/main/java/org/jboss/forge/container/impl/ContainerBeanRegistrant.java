package org.jboss.forge.container.impl;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class ContainerBeanRegistrant implements Extension
{
   public void registerWeldSEBeans(@Observes BeforeBeanDiscovery event, BeanManager manager)
   {
      // Impl types
      event.addAnnotatedType(manager.createAnnotatedType(ContainerControlImpl.class));
      // TODO Re-enable Cross Container Events
      // event.addAnnotatedType(manager.createAnnotatedType(CrossContainerObserverMethod.class));
      event.addAnnotatedType(manager.createAnnotatedType(AddonRegistryProducer.class));
      event.addAnnotatedType(manager.createAnnotatedType(AddonRepositoryProducer.class));
      event.addAnnotatedType(manager.createAnnotatedType(ForgeProducer.class));
      event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryProducer.class));
   }
}

package org.jboss.forge.furnace.impl;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.jboss.forge.furnace.events.CrossContainerObserverMethod;

public class ContainerBeanRegistrant implements Extension
{
   public void registerWeldSEBeans(@Observes BeforeBeanDiscovery event, BeanManager manager)
   {
      // TODO Re-enable Cross Container Events
      // event.addAnnotatedType(manager.createAnnotatedType(CrossContainerObserverMethod.class));
      event.addAnnotatedType(manager.createAnnotatedType(AddonProducer.class));
      event.addAnnotatedType(manager.createAnnotatedType(AddonRegistryProducer.class));
      event.addAnnotatedType(manager.createAnnotatedType(AddonRepositoryProducer.class));
      event.addAnnotatedType(manager.createAnnotatedType(CrossContainerObserverMethod.class));
      event.addAnnotatedType(manager.createAnnotatedType(FurnaceProducer.class));
      event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryProducer.class));
   }
}

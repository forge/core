package org.jboss.forge.container.impl;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.jboss.forge.container.services.RemoteProxyBeanProducerMethod;

public class BeanRegistrant implements Extension
{
   public void registerWeldSEBeans(@Observes BeforeBeanDiscovery event, BeanManager manager)
   {
      // Impl types
      event.addAnnotatedType(manager.createAnnotatedType(ContainerControlImpl.class));
      event.addAnnotatedType(manager.createAnnotatedType(AddonRegistryProducer.class));
      event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryImpl.class));
      event.addAnnotatedType(manager.createAnnotatedType(ServiceRegistryInitializer.class));
      event.addAnnotatedType(manager.createAnnotatedType(RemoteProxyBeanProducerMethod.class));
   }
}

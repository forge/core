package org.jboss.forge.container.impl;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.jboss.forge.container.event.ContainerRestart;
import org.jboss.forge.container.event.ContainerShutdown;
import org.jboss.forge.container.event.ContainerStartup;
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

      // Impl event types
      event.addAnnotatedType(manager.createAnnotatedType(ContainerRestart.class));
      event.addAnnotatedType(manager.createAnnotatedType(ContainerShutdown.class));
      event.addAnnotatedType(manager.createAnnotatedType(ContainerStartup.class));
   }
}

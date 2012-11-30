package org.jboss.forge.container.modules;

import java.util.Collections;
import java.util.List;

import org.jboss.modules.Module;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.environment.se.discovery.AbstractWeldSEDeployment;
import org.jboss.weld.resources.spi.ResourceLoader;

public class ModularWeldDeployment extends AbstractWeldSEDeployment
{
   private final BeanDeploymentArchive beanDeploymentArchive;

   public ModularWeldDeployment(Module module, Bootstrap bootstrap)
   {
      super(bootstrap);

      ModuleResourceLoader resourceLoader = new ModuleResourceLoader(module);
      this.beanDeploymentArchive = new ModularURLScanner(resourceLoader, bootstrap, RESOURCES).scan();
      this.beanDeploymentArchive.getServices().add(ResourceLoader.class, resourceLoader);
   }

   @Override
   public List<BeanDeploymentArchive> getBeanDeploymentArchives()
   {
      return Collections.singletonList(beanDeploymentArchive);
   }

   @Override
   public BeanDeploymentArchive loadBeanDeploymentArchive(Class<?> beanClass)
   {
      return beanDeploymentArchive;
   }
}

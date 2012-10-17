package org.jboss.forge.arquillian.runner;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.jboss.arquillian.testenricher.cdi.container.CDIExtension;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class BeanManagerProducer
{
   @Inject
   @ApplicationScoped
   private InstanceProducer<BeanManager> beanManagerProducer;

   public void findBeanManager(@Observes BeforeSuite context)
   {
      BeanManager beanManager = CDIExtension.getBeanManager();
      if (beanManager != null)
         beanManagerProducer.set(beanManager);
      else
         throw new IllegalStateException("Could not locate BeanManager");
   }

}
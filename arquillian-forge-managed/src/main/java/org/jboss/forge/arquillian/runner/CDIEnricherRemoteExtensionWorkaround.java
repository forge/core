package org.jboss.forge.arquillian.runner;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CDIEnricherRemoteExtensionWorkaround implements RemoteLoadableExtension
{
   @Override
   public void register(ExtensionBuilder builder)
   {
      if (Validate.classExists("javax.enterprise.inject.spi.BeanManager"))
      {
         builder.observer(BeanManagerProducer.class);
      }
      else
      {
         throw new IllegalStateException("Test not being deployed to a valid CDI environment");
      }
   }
}
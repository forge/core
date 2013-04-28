package org.jboss.forge.container.mocks;

import org.jboss.forge.container.services.Exported;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public class ServiceBean implements ServiceInterface
{
   @Override
   public Object invoke()
   {
      return "Yay!";
   }
}

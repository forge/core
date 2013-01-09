package org.jboss.forge.container;

import org.jboss.forge.container.services.Remote;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Remote
public class ServiceBean
{
   public Object invoke()
   {
      return "Yay!";
   }
}

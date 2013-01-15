package org.jboss.forge.container;

import org.jboss.forge.container.services.Exported;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public class ServiceBean
{
   public Object invoke()
   {
      return "Yay!";
   }
}

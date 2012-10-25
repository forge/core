package org.jboss.forge.otherexample;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.jboss.forge.container.event.PostStartup;
import org.jboss.forge.container.services.Remote;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Remote
@Singleton
public class PublishedService
{
   public void onStartup(@Observes PostStartup event)
   {
      System.out.println(getMessage());
   }
   
   public String getMessage()
   {
      return "I am PublishedService.";
   }

   public ClassLoader getClassLoader()
   {
      return getClass().getClassLoader();
   }
}

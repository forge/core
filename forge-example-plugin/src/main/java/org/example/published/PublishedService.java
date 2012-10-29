package org.example.published;

import javax.inject.Singleton;

import org.jboss.forge.container.services.Remote;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Remote
@Singleton
public class PublishedService
{
   public String getMessage()
   {
      return "I am PublishedService [" + hashCode() + "]";
   }

   public ClassLoader getClassLoader()
   {
      return getClass().getClassLoader();
   }
}

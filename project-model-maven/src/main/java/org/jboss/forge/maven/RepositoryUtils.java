package org.jboss.forge.maven;

import org.sonatype.aether.repository.Authentication;

/**
 * Repository Utils
 * 
 * @author George Gastaldi <gegastaldi@gmail.com>
 * 
 */
public final class RepositoryUtils
{
   private RepositoryUtils()
   {
   }

   public static org.sonatype.aether.repository.Proxy convertFromMavenProxy(org.apache.maven.settings.Proxy proxy)
   {
      org.sonatype.aether.repository.Proxy result = null;
      if (proxy != null)
      {
         Authentication auth = new Authentication(proxy.getUsername(), proxy.getPassword());
         result = new org.sonatype.aether.repository.Proxy(proxy.getProtocol(), proxy.getHost(), proxy.getPort(), auth);
      }
      return result;
   }

}

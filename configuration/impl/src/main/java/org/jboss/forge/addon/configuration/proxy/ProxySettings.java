/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration.proxy;

import java.util.Arrays;
import java.util.List;
import org.jboss.forge.addon.configuration.Configuration;

public class ProxySettings
{

   private static final String PROXY_CONFIG_HOST_KEY = "host";
   private static final String PROXY_CONFIG_PORT_KEY = "port";
   private static final String PROXY_CONFIG_USERNAME_KEY = "username";
   private static final String PROXY_CONFIG_PASSWORD_KEY = "password";
   private static final String PROXY_CONFIG_NON_PROXY_HOSTS = "nonProxyHosts";

   private final String proxyHost;
   private final int proxyPort;
   private final String proxyUserName;
   private final String proxyPassword;
   private final List<String> nonProxyHosts;

   private ProxySettings(String proxyHost, int proxyPort, String proxyUserName, String proxyPassword,
            List<String> nonProxyHosts)
   {
      this.proxyHost = proxyHost;
      this.proxyPort = proxyPort;
      this.proxyUserName = proxyUserName;
      this.proxyPassword = proxyPassword;
      this.nonProxyHosts = nonProxyHosts;
   }

   public static ProxySettings fromHostAndPort(String proxyHost, int proxyPort)
   {
      return new ProxySettings(proxyHost, proxyPort, null, null, null);
   }

   public static ProxySettings fromHostPortAndNonProxyHosts(String proxyHost, int proxyPort, List<String> nonProxyHosts)
   {
      return new ProxySettings(proxyHost, proxyPort, null, null, nonProxyHosts);
   }

   public static ProxySettings fromHostPortAndCredentials(String proxyHost, int proxyPort,
            String proxyUserName, String proxyPassword)
   {
      return new ProxySettings(proxyHost, proxyPort, proxyUserName, proxyPassword, null);
   }

   public static ProxySettings fromForgeConfiguration(Configuration configuration)
   {

      Configuration proxyConfig = configuration.subset("proxy");
      if (proxyConfig != null && !proxyConfig.isEmpty())
      {
         List<String> nonProxyHostsList = Arrays.asList(proxyConfig.getStringArray(PROXY_CONFIG_NON_PROXY_HOSTS));

         return new ProxySettings(
                  proxyConfig.getString(PROXY_CONFIG_HOST_KEY),
                  proxyConfig.getInt(PROXY_CONFIG_PORT_KEY),
                  proxyConfig.getString(PROXY_CONFIG_USERNAME_KEY),
                  proxyConfig.getString(PROXY_CONFIG_PASSWORD_KEY),
                  nonProxyHostsList
         );
      }
      else
      {
         return null;
      }
   }

   public String getProxyHost()
   {
      return proxyHost;
   }

   public int getProxyPort()
   {
      return proxyPort;
   }

   public String getProxyUserName()
   {
      return proxyUserName;
   }

   public String getProxyPassword()
   {
      return proxyPassword;
   }

   public List<String> getNonProxyHosts()
   {
      return nonProxyHosts;
   }

   public boolean isAuthenticationSupported()
   {
      return proxyUserName != null && !"".equals(proxyUserName);
   }
}

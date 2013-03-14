/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.util;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationScope;

public class ProxySettings
{

   private static final String PROXY_CONFIG_HOST_KEY = "host";
   private static final String PROXY_CONFIG_PORT_KEY = "port";
   private static final String PROXY_CONFIG_USERNAME_KEY = "username";
   private static final String PROXY_CONFIG_PASSWORD_KEY = "password";

   private final String proxyHost;
   private final int proxyPort;
   private final String proxyUserName;
   private final String proxyPassword;

   private ProxySettings(String proxyHost, int proxyPort, String proxyUserName, String proxyPassword)
   {
      this.proxyHost = proxyHost;
      this.proxyPort = proxyPort;
      this.proxyUserName = proxyUserName;
      this.proxyPassword = proxyPassword;
   }

   public static ProxySettings fromHostAndPort(String proxyHost, int proxyPort)
   {
      return new ProxySettings(proxyHost, proxyPort, null, null);
   }

   public static ProxySettings fromHostPortAndCredentials(String proxyHost, int proxyPort,
            String proxyUserName, String proxyPassword)
   {
      return new ProxySettings(proxyHost, proxyPort, proxyUserName, proxyPassword);
   }

   public static ProxySettings fromForgeConfiguration(Configuration configuration)
   {

      Configuration proxyConfig = configuration.getScopedConfiguration(
               ConfigurationScope.USER).subset("proxy");
      if (proxyConfig != null && !proxyConfig.isEmpty())
      {
         return new ProxySettings(proxyConfig.getString(PROXY_CONFIG_HOST_KEY),
                  proxyConfig.getInt(PROXY_CONFIG_PORT_KEY), proxyConfig.getString(PROXY_CONFIG_USERNAME_KEY),
                  proxyConfig.getString(PROXY_CONFIG_PASSWORD_KEY));
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

   public boolean isAuthenticationSupported()
   {
      return proxyUserName != null && !"".equals(proxyUserName);
   }
}

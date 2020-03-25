/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration.proxy;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/*
 * Implemented following the guide at:
 * http://docs.oracle.com/javase/6/docs/technotes/guides/net/proxies.html
 */
public class ForgeProxySelector extends ProxySelector
{

   private ProxySelector defaultProxySelector;
   private ProxySettings proxySettings;

   public ForgeProxySelector(ProxySelector defaultProxySelector, ProxySettings proxySettings)
   {
      this.defaultProxySelector = defaultProxySelector;
      this.proxySettings = proxySettings;
   }

   @Override
   public List<Proxy> select(URI uri)
   {
      if (uri == null)
      {
         throw new IllegalArgumentException("URI can't be null.");
      }

      if (proxySettings != null && isProxyAvailable(uri))
      {
         ArrayList<Proxy> result = new ArrayList<Proxy>();
         result.add(new Proxy(Type.HTTP, new InetSocketAddress(proxySettings.getProxyHost(),
                  proxySettings.getProxyPort())));
         if (proxySettings.isAuthenticationSupported())
         {
            Authenticator.setDefault(new Authenticator()
            {
               @Override
               protected PasswordAuthentication getPasswordAuthentication()
               {
                  return new PasswordAuthentication(proxySettings.getProxyUserName(),
                           proxySettings.getProxyPassword().toCharArray());
               }
            });
         }
         return result;
      }
      if (defaultProxySelector != null)
      {
         return defaultProxySelector.select(uri);
      }
      else
      {
         ArrayList<Proxy> result = new ArrayList<Proxy>();
         result.add(Proxy.NO_PROXY);
         return result;
      }
   }

   private boolean isProxyAvailable(final URI uri)
   {
      final String host = uri.getHost();
      final String protocol = uri.getScheme();

      boolean isValidProtocol =
               "http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol);

      return isValidProtocol && !isHostExcluded(host);
   }

   private boolean isHostExcluded(String target)
   {
         boolean result = false;
         if (proxySettings.getNonProxyHosts() != null) {
            result = proxySettings.getNonProxyHosts()
                        .stream()
                        .anyMatch(nonHost -> Pattern.matches(Globs.toUnixRegexPattern(nonHost), target));
         }
         return result;
   }

   @Override
   public void connectFailed(URI uri, SocketAddress sa, IOException ioe)
   {
      defaultProxySelector.connectFailed(uri, sa, ioe);
   }

   public ProxySelector getDefaultProxySelector()
   {
      return defaultProxySelector;
   }

}

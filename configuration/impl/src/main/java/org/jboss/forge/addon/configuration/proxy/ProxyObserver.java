/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration.proxy;

import java.net.ProxySelector;

import javax.enterprise.event.Observes;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.furnace.container.cdi.events.Local;
import org.jboss.forge.furnace.event.PostStartup;

/**
 * Sets the proxy settings in the {@link ProxySelector} as defined in the user scoped {@link Configuration}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ProxyObserver
{
   public void setProxy(@Observes @Local PostStartup event, Configuration configuration)
   {
      ProxySettings proxySettings = ProxySettings.fromForgeConfiguration(configuration);
      if (proxySettings != null)
      {
         ProxySelector defaultSelector = ProxySelector.getDefault();
         if (defaultSelector instanceof ForgeProxySelector)
         {
            defaultSelector = ((ForgeProxySelector) defaultSelector).getDefaultProxySelector();
         }
         ForgeProxySelector selector = new ForgeProxySelector(defaultSelector, proxySettings);
         ProxySelector.setDefault(selector);
      }
   }
}
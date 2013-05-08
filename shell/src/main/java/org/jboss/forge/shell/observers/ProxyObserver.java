/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.observers;

import java.net.ProxySelector;

import javax.enterprise.event.Observes;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.shell.events.Startup;
import org.jboss.forge.shell.util.ForgeProxySelector;
import org.jboss.forge.shell.util.ProxySettings;

/**
 * Configures a proxy to be used in the whole project
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class ProxyObserver
{
   public void setProxy(@Observes Startup event, Configuration configuration)
   {
      ProxySettings proxySettings = ProxySettings.fromForgeConfiguration(configuration);
      if (proxySettings == null)
      {
         // There is no proxy configured
         return;
      }
      ProxySelector defaultSelector = ProxySelector.getDefault();
      if (defaultSelector instanceof ForgeProxySelector)
      {
         defaultSelector = ((ForgeProxySelector) defaultSelector).getDefaultProxySelector();
      }
      ForgeProxySelector selector = new ForgeProxySelector(defaultSelector, proxySettings);
      ProxySelector.setDefault(selector);
   }
}

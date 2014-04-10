/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Vetoed
public class ConfigurationAdapterSubset extends ConfigurationAdapter
{
   private final HierarchicalConfiguration parent;
   private final String prefix;

   public ConfigurationAdapterSubset(HierarchicalConfiguration delegate, String prefix)
   {
      this.parent = delegate;
      this.prefix = prefix;

      synchronized (delegate)
      {
         if (delegate.containsKey(prefix))
            setDelegate(delegate.configurationAt(prefix, true));
         else
            setDelegate((HierarchicalConfiguration) delegate.subset(prefix));
      }
   }

   @Override
   public void setProperty(final String key, final Object value)
   {
      synchronized (parent)
      {
         if (!parent.containsKey(prefix))
         {
            parent.setProperty(prefix, "");
            setDelegate(parent.configurationAt(prefix));
         }
         getDelegate().setProperty(key, value);
      }
   }

}

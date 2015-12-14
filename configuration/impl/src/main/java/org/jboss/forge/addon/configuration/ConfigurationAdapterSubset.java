/*
* Copyright 2014 Red Hat, Inc. and/or its affiliates.
*
* Licensed under the Eclipse Public License version 1.0, available at
* http://www.eclipse.org/legal/epl-v10.html
*/
package org.jboss.forge.addon.configuration;

import java.util.Collections;
import java.util.Iterator;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:danielsoro@gmail.com">Daniel Cunha (soro)</a>
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
   public Iterator<String> getKeys()
   {
      synchronized (parent)
      {
         try
         {
            return parent.configurationAt(prefix).getKeys();
         }
         catch (IllegalArgumentException e)
         {
            return Collections.emptyIterator();
         }
      }
   }

   @Override
   public void clearProperty(String key)
   {
      synchronized (parent)
      {
         try
         {
            parent.configurationAt(prefix).clearProperty(key);
         }
         catch (IllegalArgumentException e)
         {
            // do nothing;
         }
      }
   }

   @Override
   public void setProperty(final String key, final Object value)
   {
      synchronized (parent)
      {
         try
         {
            parent.configurationAt(prefix).setProperty(key, value);
         }
         catch (IllegalArgumentException e)
         {
            parent.setProperty(prefix + "." + key, value);
         }
      }
   }
}
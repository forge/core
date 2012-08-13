/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.env;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.inject.Typed;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationScope;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Typed()
public class ScopedConfigurationAdapter implements Configuration
{
   private final Map<ConfigurationScope, Configuration> delegates = new LinkedHashMap<ConfigurationScope, Configuration>();

   public ScopedConfigurationAdapter(final ConfigurationScope scope, final Configuration delegate)
   {
      delegates.put(scope, delegate);
   }

   public ScopedConfigurationAdapter()
   {
   }

   public Configuration getDelegate()
   {
      for (Configuration config : delegates.values())
      {
         if (config != null)
         {
            return config;
         }
      }
      return null;
   }

   @Override
   public Configuration getScopedConfiguration(final ConfigurationScope scope)
   {
      Configuration configuration = delegates.get(scope);
      if (configuration == null)
      {
         throw new IllegalStateException("No delegates were found in configuration - cannot retrieve scope");
      }
      return configuration;
   }

   public void setScopedConfiguration(final ConfigurationScope user, final Configuration config)
   {
      delegates.put(user, config);
   }

   @Override
   public Configuration subset(final String prefix)
   {
      ScopedConfigurationAdapter result = new ScopedConfigurationAdapter();
      for (Entry<ConfigurationScope, Configuration> entry : delegates.entrySet())
      {
         result.setScopedConfiguration(entry.getKey(), entry.getValue().subset(prefix));
      }
      return result;
   }

   @Override
   public boolean isEmpty()
   {
      for (Configuration config : delegates.values())
      {
         if (!config.isEmpty())
         {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean containsKey(final String key)
   {
      for (Configuration config : delegates.values())
      {
         if (config.containsKey(key))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public void addProperty(final String key, final Object value)
   {
      getDelegate().addProperty(key, value);
   }

   @Override
   public void setProperty(final String key, final Object value)
   {
      getDelegate().setProperty(key, value);
   }

   @Override
   public void clearProperty(final String key)
   {
      for (Configuration config : delegates.values())
      {
         config.clearProperty(key);
      }
   }

   @Override
   public void clear()
   {
      for (Configuration config : delegates.values())
      {
         config.clear();
      }
   }

   @Override
   public Object getProperty(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return property;
         }
      }
      return null;
   }

   @Override
   public Iterator<?> getKeys(final String prefix)
   {
      Set<Object> keys = new HashSet<Object>();
      for (Configuration config : delegates.values())
      {
         Iterator<?> iterator = config.getKeys(prefix);
         while (iterator.hasNext())
         {
            keys.add(iterator.next());
         }
      }
      return keys.iterator();
   }

   @Override
   public Iterator<?> getKeys()
   {
      Set<Object> keys = new HashSet<Object>();
      for (Configuration config : delegates.values())
      {
         Iterator<?> iterator = config.getKeys();
         while (iterator.hasNext())
         {
            keys.add(iterator.next());
         }
      }
      return keys.iterator();
   }

   @Override
   public Properties getProperties(final String key)
   {
      Properties result = new Properties();
      for (Configuration config : delegates.values())
      {
         result.putAll(config.getProperties(key));
      }
      return result;
   }

   @Override
   public boolean getBoolean(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getBoolean(key);
         }
      }
      return getDelegate().getBoolean(key);
   }

   @Override
   public boolean getBoolean(final String key, final boolean defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getBoolean(key);
         }
      }
      return defaultValue;
   }

   @Override
   public Boolean getBoolean(final String key, final Boolean defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getBoolean(key);
         }
      }
      return defaultValue;
   }

   @Override
   public byte getByte(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getByte(key);
         }
      }
      return getDelegate().getByte(key);
   }

   @Override
   public byte getByte(final String key, final byte defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getByte(key);
         }
      }
      return defaultValue;
   }

   @Override
   public Byte getByte(final String key, final Byte defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getByte(key);
         }
      }
      return defaultValue;
   }

   @Override
   public double getDouble(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getDouble(key);
         }
      }
      return getDelegate().getDouble(key);
   }

   @Override
   public double getDouble(final String key, final double defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getDouble(key);
         }
      }
      return defaultValue;
   }

   @Override
   public Double getDouble(final String key, final Double defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getDouble(key);
         }
      }
      return defaultValue;
   }

   @Override
   public float getFloat(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getFloat(key);
         }
      }
      return getDelegate().getFloat(key);
   }

   @Override
   public float getFloat(final String key, final float defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getFloat(key);
         }
      }
      return defaultValue;
   }

   @Override
   public Float getFloat(final String key, final Float defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getFloat(key);
         }
      }
      return defaultValue;
   }

   @Override
   public int getInt(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getInt(key);
         }
      }
      return getDelegate().getInt(key);
   }

   @Override
   public int getInt(final String key, final int defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getInt(key);
         }
      }
      return defaultValue;
   }

   @Override
   public Integer getInteger(final String key, final Integer defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getInt(key);
         }
      }
      return defaultValue;
   }

   @Override
   public long getLong(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getLong(key);
         }
      }
      return getDelegate().getLong(key);
   }

   @Override
   public long getLong(final String key, final long defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getLong(key);
         }
      }
      return defaultValue;
   }

   @Override
   public Long getLong(final String key, final Long defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getLong(key);
         }
      }
      return defaultValue;
   }

   @Override
   public short getShort(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getShort(key);
         }
      }
      return getDelegate().getShort(key);
   }

   @Override
   public short getShort(final String key, final short defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getShort(key);
         }
      }
      return defaultValue;
   }

   @Override
   public Short getShort(final String key, final Short defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getShort(key);
         }
      }
      return defaultValue;
   }

   @Override
   public BigDecimal getBigDecimal(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getBigDecimal(key);
         }
      }
      return getDelegate().getBigDecimal(key);
   }

   @Override
   public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getBigDecimal(key);
         }
      }
      return defaultValue;
   }

   @Override
   public BigInteger getBigInteger(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getBigInteger(key);
         }
      }
      return getDelegate().getBigInteger(key);
   }

   @Override
   public BigInteger getBigInteger(final String key, final BigInteger defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getBigInteger(key);
         }
      }
      return defaultValue;
   }

   @Override
   public String getString(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getString(key);
         }
      }
      return getDelegate().getString(key);
   }

   @Override
   public String getString(final String key, final String defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getString(key);
         }
      }
      return defaultValue;
   }

   @Override
   public String[] getStringArray(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getStringArray(key);
         }
      }
      return getDelegate().getStringArray(key);
   }

   @Override
   public List<?> getList(final String key)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getList(key);
         }
      }
      return getDelegate().getList(key);
   }

   @Override
   public List<?> getList(final String key, final List<?> defaultValue)
   {
      for (Configuration config : delegates.values())
      {
         Object property = config.getProperty(key);
         if (property != null)
         {
            return config.getList(key);
         }
      }
      return defaultValue;
   }

}

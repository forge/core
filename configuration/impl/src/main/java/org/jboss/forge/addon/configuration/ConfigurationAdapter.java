/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.furnace.util.Iterators;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
@Vetoed
public class ConfigurationAdapter implements Configuration
{
   private final org.apache.commons.configuration.Configuration delegate;

   public ConfigurationAdapter(final org.apache.commons.configuration.Configuration delegate)
   {
      this.delegate = delegate;
   }

   protected org.apache.commons.configuration.Configuration getDelegate()
   {
      return delegate;
   }

   /*
    * Configuration methods.
    */

   @Override
   public Configuration subset(final String prefix)
   {
      return new ConfigurationAdapterSubset(getDelegate(), prefix);
   }

   @Override
   public boolean isEmpty()
   {
      return getDelegate().isEmpty();
   }

   @Override
   public boolean containsKey(final String key)
   {
      return getDelegate().containsKey(key);
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
      getDelegate().clearProperty(key);
   }

   @Override
   public void clear()
   {
      getDelegate().clear();
   }

   @Override
   public Object getProperty(final String key)
   {
      return getDelegate().getProperty(key);
   }

   @Override
   public Iterator<String> getKeys(final String prefix)
   {
      // FORGE-1971: getDelegate().getKeys(prefix) returns an empty string as the key
      List<String> list = Iterators.asList(getDelegate().getKeys(prefix));
      list.remove("");
      return list.iterator();
   }

   @Override
   public Iterator<String> getKeys()
   {
      // FORGE-1971: getDelegate().getKeys() returns an empty string as the key
      List<String> list = Iterators.asList(getDelegate().getKeys());
      list.remove("");
      return list.iterator();
   }

   @Override
   public Properties getProperties(final String key)
   {
      return getDelegate().getProperties(key);
   }

   @Override
   public boolean getBoolean(final String key)
   {
      return getDelegate().getBoolean(key);
   }

   @Override
   public boolean getBoolean(final String key, final boolean defaultValue)
   {
      return getDelegate().getBoolean(key, defaultValue);
   }

   @Override
   public Boolean getBoolean(final String key, final Boolean defaultValue)
   {
      return getDelegate().getBoolean(key, defaultValue);
   }

   @Override
   public byte getByte(final String key)
   {
      return getDelegate().getByte(key);
   }

   @Override
   public byte getByte(final String key, final byte defaultValue)
   {
      return getDelegate().getByte(key, defaultValue);
   }

   @Override
   public Byte getByte(final String key, final Byte defaultValue)
   {
      return getDelegate().getByte(key, defaultValue);
   }

   @Override
   public double getDouble(final String key)
   {
      return getDelegate().getDouble(key);
   }

   @Override
   public double getDouble(final String key, final double defaultValue)
   {
      return getDelegate().getDouble(key, defaultValue);
   }

   @Override
   public Double getDouble(final String key, final Double defaultValue)
   {
      return getDelegate().getDouble(key, defaultValue);
   }

   @Override
   public float getFloat(final String key)
   {
      return getDelegate().getFloat(key);
   }

   @Override
   public float getFloat(final String key, final float defaultValue)
   {
      return getDelegate().getFloat(key, defaultValue);
   }

   @Override
   public Float getFloat(final String key, final Float defaultValue)
   {
      return getDelegate().getFloat(key, defaultValue);
   }

   @Override
   public int getInt(final String key)
   {
      return getDelegate().getInt(key);
   }

   @Override
   public int getInt(final String key, final int defaultValue)
   {
      return getDelegate().getInt(key, defaultValue);
   }

   @Override
   public Integer getInteger(final String key, final Integer defaultValue)
   {
      return getDelegate().getInteger(key, defaultValue);
   }

   @Override
   public long getLong(final String key)
   {
      return getDelegate().getLong(key);
   }

   @Override
   public long getLong(final String key, final long defaultValue)
   {
      return getDelegate().getLong(key, defaultValue);
   }

   @Override
   public Long getLong(final String key, final Long defaultValue)
   {
      return getDelegate().getLong(key, defaultValue);
   }

   @Override
   public short getShort(final String key)
   {
      return getDelegate().getShort(key);
   }

   @Override
   public short getShort(final String key, final short defaultValue)
   {
      return getDelegate().getShort(key, defaultValue);
   }

   @Override
   public Short getShort(final String key, final Short defaultValue)
   {
      return getDelegate().getShort(key, defaultValue);
   }

   @Override
   public BigDecimal getBigDecimal(final String key)
   {
      return getDelegate().getBigDecimal(key);
   }

   @Override
   public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue)
   {
      return getDelegate().getBigDecimal(key, defaultValue);
   }

   @Override
   public BigInteger getBigInteger(final String key)
   {
      return getDelegate().getBigInteger(key);
   }

   @Override
   public BigInteger getBigInteger(final String key, final BigInteger defaultValue)
   {
      return getDelegate().getBigInteger(key, defaultValue);
   }

   @Override
   public String getString(final String key)
   {
      return getDelegate().getString(key);
   }

   @Override
   public String getString(final String key, final String defaultValue)
   {
      return getDelegate().getString(key, defaultValue);
   }

   @Override
   public String[] getStringArray(final String key)
   {
      return getDelegate().getStringArray(key);
   }

   @Override
   public List<Object> getList(final String key)
   {
      return getDelegate().getList(key);
   }

   @Override
   public List<Object> getList(final String key, final List<?> defaultValue)
   {
      return getDelegate().getList(key, defaultValue);
   }

}

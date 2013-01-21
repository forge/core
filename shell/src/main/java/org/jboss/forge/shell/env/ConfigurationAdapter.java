/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.env;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationScope;
import org.jboss.forge.shell.squelch.ConfigAdapterQualifierLiteral;
import org.jboss.forge.shell.squelch.Squelched;
import org.jboss.forge.shell.util.BeanManagerUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Dependent
@Squelched
@ConfigAdapterQualifier
public class ConfigurationAdapter implements Configuration
{
   private ScopedConfigurationAdapter parent;
   private org.apache.commons.configuration.Configuration delegate;
   private BeanManager bm;

   public ConfigurationAdapter()
   {
   }

   public ConfigurationAdapter(final ScopedConfigurationAdapter parent,
            final org.apache.commons.configuration.Configuration delegate)
   {
      this.parent = parent;
      this.delegate = delegate;
   }

   public ConfigurationAdapter(org.apache.commons.configuration.Configuration delegate)
   {
      this.parent = null;
      this.delegate = delegate;
   }

   public org.apache.commons.configuration.Configuration getDelegate()
   {
      return delegate;
   }

   public void setParent(ScopedConfigurationAdapter parent)
   {
      this.parent = parent;
   }

   public void setDelegate(org.apache.commons.configuration.Configuration delegate)
   {
      this.delegate = delegate;
   }

   public void setBeanManager(BeanManager bm)
   {
      this.bm = bm;
   }

   @Override
   public Configuration getScopedConfiguration(final ConfigurationScope scope)
   {
      if (parent == null)
      {
         throw new IllegalArgumentException("No Configuration registered to scope type: " + scope);
      }
      return parent.getScopedConfiguration(scope);
   }

   @Override
   public Configuration subset(final String prefix)
   {

      ConfigurationAdapter adapter = BeanManagerUtils.getContextualInstance(bm, ConfigurationAdapter.class,
               new ConfigAdapterQualifierLiteral());
      adapter.setParent(parent);
      adapter.setDelegate(delegate.subset(prefix));
      adapter.setBeanManager(bm);
      return adapter;
   }

   @Override
   public boolean isEmpty()
   {
      return delegate.isEmpty();
   }

   @Override
   public boolean containsKey(final String key)
   {
      return delegate.containsKey(key);
   }

   @Override
   public void addProperty(final String key, final Object value)
   {
      delegate.addProperty(key, value);
   }

   @Override
   public void setProperty(final String key, final Object value)
   {
      delegate.setProperty(key, value);
   }

   @Override
   public void clearProperty(final String key)
   {
      delegate.clearProperty(key);
   }

   @Override
   public void clear()
   {
      delegate.clear();
   }

   @Override
   public Object getProperty(final String key)
   {
      return delegate.getProperty(key);
   }

   @Override
   public Iterator<?> getKeys(final String prefix)
   {
      return delegate.getKeys(prefix);
   }

   @Override
   public Iterator<?> getKeys()
   {
      return delegate.getKeys();
   }

   @Override
   public Properties getProperties(final String key)
   {
      return delegate.getProperties(key);
   }

   @Override
   public boolean getBoolean(final String key)
   {
      return delegate.getBoolean(key);
   }

   @Override
   public boolean getBoolean(final String key, final boolean defaultValue)
   {
      return delegate.getBoolean(key, defaultValue);
   }

   @Override
   public Boolean getBoolean(final String key, final Boolean defaultValue)
   {
      return delegate.getBoolean(key, defaultValue);
   }

   @Override
   public byte getByte(final String key)
   {
      return delegate.getByte(key);
   }

   @Override
   public byte getByte(final String key, final byte defaultValue)
   {
      return delegate.getByte(key, defaultValue);
   }

   @Override
   public Byte getByte(final String key, final Byte defaultValue)
   {
      return delegate.getByte(key, defaultValue);
   }

   @Override
   public double getDouble(final String key)
   {
      return delegate.getDouble(key);
   }

   @Override
   public double getDouble(final String key, final double defaultValue)
   {
      return delegate.getDouble(key, defaultValue);
   }

   @Override
   public Double getDouble(final String key, final Double defaultValue)
   {
      return delegate.getDouble(key, defaultValue);
   }

   @Override
   public float getFloat(final String key)
   {
      return delegate.getFloat(key);
   }

   @Override
   public float getFloat(final String key, final float defaultValue)
   {
      return delegate.getFloat(key, defaultValue);
   }

   @Override
   public Float getFloat(final String key, final Float defaultValue)
   {
      return delegate.getFloat(key, defaultValue);
   }

   @Override
   public int getInt(final String key)
   {
      return delegate.getInt(key);
   }

   @Override
   public int getInt(final String key, final int defaultValue)
   {
      return delegate.getInt(key, defaultValue);
   }

   @Override
   public Integer getInteger(final String key, final Integer defaultValue)
   {
      return delegate.getInteger(key, defaultValue);
   }

   @Override
   public long getLong(final String key)
   {
      return delegate.getLong(key);
   }

   @Override
   public long getLong(final String key, final long defaultValue)
   {
      return delegate.getLong(key, defaultValue);
   }

   @Override
   public Long getLong(final String key, final Long defaultValue)
   {
      return delegate.getLong(key, defaultValue);
   }

   @Override
   public short getShort(final String key)
   {
      return delegate.getShort(key);
   }

   @Override
   public short getShort(final String key, final short defaultValue)
   {
      return delegate.getShort(key, defaultValue);
   }

   @Override
   public Short getShort(final String key, final Short defaultValue)
   {
      return delegate.getShort(key, defaultValue);
   }

   @Override
   public BigDecimal getBigDecimal(final String key)
   {
      return delegate.getBigDecimal(key);
   }

   @Override
   public BigDecimal getBigDecimal(final String key, final BigDecimal defaultValue)
   {
      return delegate.getBigDecimal(key, defaultValue);
   }

   @Override
   public BigInteger getBigInteger(final String key)
   {
      return delegate.getBigInteger(key);
   }

   @Override
   public BigInteger getBigInteger(final String key, final BigInteger defaultValue)
   {
      return delegate.getBigInteger(key, defaultValue);
   }

   @Override
   public String getString(final String key)
   {
      return delegate.getString(key);
   }

   @Override
   public String getString(final String key, final String defaultValue)
   {
      return delegate.getString(key, defaultValue);
   }

   @Override
   public String[] getStringArray(final String key)
   {
      return delegate.getStringArray(key);
   }

   @Override
   public List<?> getList(final String key)
   {
      return delegate.getList(key);
   }

   @Override
   public List<?> getList(final String key, final List<?> defaultValue)
   {
      return delegate.getList(key, defaultValue);
   }

}

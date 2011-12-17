/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.shell.env;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationScope;
import org.jboss.solder.core.Veto;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Veto
public class ConfigurationAdapter implements Configuration
{
   private final ScopedConfigurationAdapter parent;
   private final org.apache.commons.configuration.Configuration delegate;

   public ConfigurationAdapter(final ScopedConfigurationAdapter parent,
            final org.apache.commons.configuration.Configuration delegate)
   {
      this.parent = parent;
      this.delegate = delegate;
   }

   public org.apache.commons.configuration.Configuration getDelegate()
   {
      return delegate;
   }

   @Override
   public Configuration getScopedConfiguration(final ConfigurationScope scope)
   {
      return parent.getScopedConfiguration(scope);
   }

   @Override
   public Configuration subset(final String prefix)
   {
      return new ConfigurationAdapter(parent, delegate.subset(prefix));
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

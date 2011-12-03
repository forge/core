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
package org.jboss.forge.env;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jboss.solder.core.Veto;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Veto
public class ScopedConfigurationAdapter implements Configuration
{
   private final Map<ConfigurationScope, Configuration> delegates = new LinkedHashMap<ConfigurationScope, Configuration>();

   public ScopedConfigurationAdapter(final ConfigurationScope scope, final Configuration delegate)
   {
      delegates.put(scope, delegate);
   }

   public ScopedConfigurationAdapter()
   {}

   public Configuration getDelegate()
   {
      return this;
   }

   @Override
   public Configuration getScopedConfiguration(final ConfigurationScope scope)
   {
      Configuration configuration = delegates.get(scope);
      if (configuration == null)
      {
         throw new IllegalStateException();
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
      for (Entry<ConfigurationScope, Configuration> entry : delegates.entrySet()) {
         result.setScopedConfiguration(entry.getKey(), entry.getValue().subset(prefix));
      }
      return result;
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
   public Iterator<?> getKeys(final String prefix)
   {
      return getDelegate().getKeys(prefix);
   }

   @Override
   public Iterator<?> getKeys()
   {
      return getDelegate().getKeys();
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
   public List<?> getList(final String key)
   {
      return getDelegate().getList(key);
   }

   @Override
   public List<?> getList(final String key, final List<?> defaultValue)
   {
      return getDelegate().getList(key, defaultValue);
   }

}

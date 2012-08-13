/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.metawidget.widgetbuilder;

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
public class MockForgeConfiguration implements Configuration
{

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getScopedConfiguration(org.jboss.forge.env.ConfigurationScope)
    */
   @Override
   public Configuration getScopedConfiguration(ConfigurationScope scope)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#subset(java.lang.String)
    */
   @Override
   public Configuration subset(String prefix)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#isEmpty()
    */
   @Override
   public boolean isEmpty()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#containsKey(java.lang.String)
    */
   @Override
   public boolean containsKey(String key)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#addProperty(java.lang.String, java.lang.Object)
    */
   @Override
   public void addProperty(String key, Object value)
   {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#setProperty(java.lang.String, java.lang.Object)
    */
   @Override
   public void setProperty(String key, Object value)
   {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#clearProperty(java.lang.String)
    */
   @Override
   public void clearProperty(String key)
   {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#clear()
    */
   @Override
   public void clear()
   {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getProperty(java.lang.String)
    */
   @Override
   public Object getProperty(String key)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getKeys(java.lang.String)
    */
   @Override
   public Iterator<?> getKeys(String prefix)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getKeys()
    */
   @Override
   public Iterator<?> getKeys()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getProperties(java.lang.String)
    */
   @Override
   public Properties getProperties(String key)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getBoolean(java.lang.String)
    */
   @Override
   public boolean getBoolean(String key)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getBoolean(java.lang.String, boolean)
    */
   @Override
   public boolean getBoolean(String key, boolean defaultValue)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getBoolean(java.lang.String, java.lang.Boolean)
    */
   @Override
   public Boolean getBoolean(String key, Boolean defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getByte(java.lang.String)
    */
   @Override
   public byte getByte(String key)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getByte(java.lang.String, byte)
    */
   @Override
   public byte getByte(String key, byte defaultValue)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getByte(java.lang.String, java.lang.Byte)
    */
   @Override
   public Byte getByte(String key, Byte defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getDouble(java.lang.String)
    */
   @Override
   public double getDouble(String key)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getDouble(java.lang.String, double)
    */
   @Override
   public double getDouble(String key, double defaultValue)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getDouble(java.lang.String, java.lang.Double)
    */
   @Override
   public Double getDouble(String key, Double defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getFloat(java.lang.String)
    */
   @Override
   public float getFloat(String key)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getFloat(java.lang.String, float)
    */
   @Override
   public float getFloat(String key, float defaultValue)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getFloat(java.lang.String, java.lang.Float)
    */
   @Override
   public Float getFloat(String key, Float defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getInt(java.lang.String)
    */
   @Override
   public int getInt(String key)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getInt(java.lang.String, int)
    */
   @Override
   public int getInt(String key, int defaultValue)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getInteger(java.lang.String, java.lang.Integer)
    */
   @Override
   public Integer getInteger(String key, Integer defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getLong(java.lang.String)
    */
   @Override
   public long getLong(String key)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getLong(java.lang.String, long)
    */
   @Override
   public long getLong(String key, long defaultValue)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getLong(java.lang.String, java.lang.Long)
    */
   @Override
   public Long getLong(String key, Long defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getShort(java.lang.String)
    */
   @Override
   public short getShort(String key)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getShort(java.lang.String, short)
    */
   @Override
   public short getShort(String key, short defaultValue)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getShort(java.lang.String, java.lang.Short)
    */
   @Override
   public Short getShort(String key, Short defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getBigDecimal(java.lang.String)
    */
   @Override
   public BigDecimal getBigDecimal(String key)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getBigDecimal(java.lang.String, java.math.BigDecimal)
    */
   @Override
   public BigDecimal getBigDecimal(String key, BigDecimal defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getBigInteger(java.lang.String)
    */
   @Override
   public BigInteger getBigInteger(String key)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getBigInteger(java.lang.String, java.math.BigInteger)
    */
   @Override
   public BigInteger getBigInteger(String key, BigInteger defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getString(java.lang.String)
    */
   @Override
   public String getString(String key)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getString(java.lang.String, java.lang.String)
    */
   @Override
   public String getString(String key, String defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getStringArray(java.lang.String)
    */
   @Override
   public String[] getStringArray(String key)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getList(java.lang.String)
    */
   @Override
   public List<?> getList(String key)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.jboss.forge.env.Configuration#getList(java.lang.String, java.util.List)
    */
   @Override
   public List<?> getList(String key, List<?> defaultValue)
   {
      // TODO Auto-generated method stub
      return null;
   }

}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.convert;

import static java.lang.String.valueOf;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mvel2.ConversionException;
import org.mvel2.ConversionHandler;
import org.mvel2.conversion.Converter;

/**
 * User: christopherbrock Date: 31-Aug-2010 Time: 10:58:26 PM
 */
public class BooleanConverter implements ConversionHandler
{
   private static final Map<Class<?>, Converter> CNV =
            new HashMap<Class<?>, Converter>();

   private static final Set<Object> TRUE = new HashSet<Object>();
   private static final Set<Object> FALSE = new HashSet<Object>();

   static
   {
      TRUE.add("true");
      TRUE.add("yes");
      TRUE.add("y");
      TRUE.add("on");

      FALSE.add("false");
      FALSE.add("no");
      FALSE.add("n");
      FALSE.add("off");
   }

   private static Converter stringConverter = new Converter()
   {
      @Override
      public Object convert(final Object o)
      {
         final String s = ((String) o).toLowerCase();
         if (TRUE.contains(s))
         {
            return true;
         }
         else if (FALSE.contains(s))
         {
            return false;
         }
         throw new IllegalArgumentException("not a valid boolean: '" + s + "'");
      }
   };

   @Override
   public Object convertFrom(final Object in)
   {
      if (!CNV.containsKey(in.getClass()))
      {
         throw new ConversionException("cannot convert type: "
                  + in.getClass().getName() + " to: " + Boolean.class.getName());
      }
      return CNV.get(in.getClass()).convert(in);
   }

   @Override
   @SuppressWarnings("rawtypes")
   public boolean canConvertFrom(final Class cls)
   {
      return CNV.containsKey(cls);
   }

   static
   {
      CNV.put(String.class,
               stringConverter
               );

      CNV.put(Object.class,
               new Converter()
            {
               @Override
               public Object convert(final Object o)
               {
                  return stringConverter.convert(valueOf(o));
               }
            }
               );

      CNV.put(Boolean.class,
               new Converter()
            {
               @Override
               public Object convert(final Object o)
               {
                  return o;
               }
            }
               );

      CNV.put(Integer.class,
               new Converter()
            {
               @Override
               public Boolean convert(final Object o)
               {
                  return (((Integer) o) > 0);
               }
            }
               );

      CNV.put(Float.class,
               new Converter()
            {
               @Override
               public Boolean convert(final Object o)
               {
                  return (((Float) o) > 0);
               }
            }
               );

      CNV.put(Double.class,
               new Converter()
            {
               @Override
               public Boolean convert(final Object o)
               {
                  return (((Double) o) > 0);
               }
            }
               );

      CNV.put(Short.class,
               new Converter()
            {
               @Override
               public Boolean convert(final Object o)
               {
                  return (((Short) o) > 0);
               }
            }
               );

      CNV.put(Long.class,
               new Converter()
            {
               @Override
               public Boolean convert(final Object o)
               {
                  return (((Long) o) > 0);
               }
            }
               );

      CNV.put(boolean.class,
               new Converter()
            {

               @Override
               public Boolean convert(final Object o)
               {
                  return Boolean.valueOf((Boolean) o);
               }
            }
               );

      CNV.put(BigDecimal.class,
               new Converter()
            {

               @Override
               public Boolean convert(final Object o)
               {
                  return Boolean.valueOf(((BigDecimal) o).doubleValue() > 0);
               }
            }
               );

   }
}

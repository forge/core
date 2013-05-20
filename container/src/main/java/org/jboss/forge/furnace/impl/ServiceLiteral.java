package org.jboss.forge.furnace.impl;

import java.lang.annotation.Annotation;

final class ServiceLiteral implements Service
{
   private static int INSTANCE_COUNT = 0;

   private int id;

   public ServiceLiteral()
   {
      this.id = uniqueId();
   }

   @Override
   public Class<? extends Annotation> annotationType()
   {
      return Service.class;
   }

   @Override
   public int id()
   {
      return id;
   }

   public static int uniqueId()
   {
      return INSTANCE_COUNT++;
   }
}
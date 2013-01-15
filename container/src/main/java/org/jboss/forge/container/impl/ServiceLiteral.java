package org.jboss.forge.container.impl;

import java.lang.annotation.Annotation;

final class ServiceLiteral implements Service
{
   @Override
   public Class<? extends Annotation> annotationType()
   {
      return Service.class;
   }
}
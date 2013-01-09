/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.impl.converter.cdi;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;

import org.jboss.forge.ui.converter.ConverterRegistry;
import org.jboss.forge.ui.impl.ConverterRegistryImpl;

public class ConverterRegistryProducer
{
   @Produces
   @Default
   @Singleton
   public ConverterRegistry produce(InjectionPoint injectionPoint)
   {
      return ConverterRegistryImpl.INSTANCE;
   }
}

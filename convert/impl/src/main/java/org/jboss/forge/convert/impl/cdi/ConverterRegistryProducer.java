/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.convert.impl.cdi;

import javax.enterprise.inject.Produces;

import org.jboss.forge.convert.ConverterRegistry;
import org.jboss.forge.convert.impl.ConverterRegistryImpl;

public class ConverterRegistryProducer
{
   @Produces
   public ConverterRegistry produce()
   {
      return ConverterRegistryImpl.INSTANCE;
   }
}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.converter.impl.cdi;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.converter.StringConverterRegistry;
import org.jboss.forge.converter.impl.StringConverterRegistryImpl;

/**
 * Produces a {@link StringConverterRegistry}
 *
 * NOTE: Discuss about this class
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class StringConverterRegistryProducer
{
   @Produces
   public StringConverterRegistry produce(InjectionPoint injectionPoint)
   {
      return StringConverterRegistryImpl.INSTANCE;
   }
}

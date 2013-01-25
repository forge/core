/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.convert.impl;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.convert.BaseConverter;

@Vetoed
public class NoopConverter<S> extends BaseConverter<S, S>
{
   public NoopConverter(Class<S> type)
   {
      super(type, type);
   }

   @Override
   public S convert(S source)
   {
      return source;
   }

}

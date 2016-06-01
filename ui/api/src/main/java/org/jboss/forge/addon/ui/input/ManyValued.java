/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import java.util.concurrent.Callable;

/**
 * UI components implementing this interface hold multiple values
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <IMPL>
 * @param <VALUETYPE>
 */
public interface ManyValued<IMPL, VALUETYPE>
{
   Iterable<VALUETYPE> getValue();

   IMPL setDefaultValue(Iterable<VALUETYPE> value);

   IMPL setDefaultValue(Callable<Iterable<VALUETYPE>> callback);

   IMPL setValue(Iterable<VALUETYPE> value);

}

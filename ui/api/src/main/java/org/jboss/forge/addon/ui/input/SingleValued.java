/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import java.util.concurrent.Callable;

/**
 * UI components implementing this interface hold a single value
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <IMPL>
 * @param <VALUETYPE>
 */
public interface SingleValued<IMPL, VALUETYPE>
{
   VALUETYPE getValue();

   IMPL setDefaultValue(VALUETYPE value);

   IMPL setDefaultValue(Callable<VALUETYPE> callback);

   IMPL setValue(VALUETYPE value);
}

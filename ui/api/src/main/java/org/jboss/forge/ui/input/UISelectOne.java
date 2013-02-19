/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.input;

import java.util.concurrent.Callable;

public interface UISelectOne<VALUETYPE> extends UIInputComponent<UISelectOne<VALUETYPE>, VALUETYPE>
{
   Iterable<VALUETYPE> getValueChoices();

   UISelectOne<VALUETYPE> setValueChoices(Iterable<VALUETYPE> values);

   VALUETYPE getValue();

   UISelectOne<VALUETYPE> setDefaultValue(VALUETYPE value);

   UISelectOne<VALUETYPE> setDefaultValue(Callable<VALUETYPE> callback);

   UISelectOne<VALUETYPE> setValue(VALUETYPE value);
}

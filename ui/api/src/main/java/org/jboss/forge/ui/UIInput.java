/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;

import java.util.concurrent.Callable;

public interface UIInput<VALUETYPE> extends UIInputComponent<UIInput<VALUETYPE>, VALUETYPE>
{
   UICompleter<VALUETYPE> getCompleter();

   UIInput<VALUETYPE> setCompleter(UICompleter<VALUETYPE> completer);

   VALUETYPE getValue();

   UIInput<VALUETYPE> setDefaultValue(VALUETYPE value);

   UIInput<VALUETYPE> setDefaultValue(Callable<VALUETYPE> callback);

   UIInput<VALUETYPE> setValue(VALUETYPE value);
}
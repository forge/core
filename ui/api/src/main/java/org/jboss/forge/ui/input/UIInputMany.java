/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.input;

import java.util.concurrent.Callable;

import org.jboss.forge.ui.UICompleter;

public interface UIInputMany<VALUETYPE> extends UIInputComponent<UIInputMany<VALUETYPE>, VALUETYPE>
{
   UICompleter<VALUETYPE> getCompleter();

   UIInputMany<VALUETYPE> setCompleter(UICompleter<VALUETYPE> completer);

   Iterable<VALUETYPE> getValue();

   UIInputMany<VALUETYPE> setDefaultValue(Iterable<VALUETYPE> value);

   UIInputMany<VALUETYPE> setDefaultValue(Callable<Iterable<VALUETYPE>> callback);

   UIInputMany<VALUETYPE> setValue(Iterable<VALUETYPE> value);
}
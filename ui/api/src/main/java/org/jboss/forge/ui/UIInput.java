/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;

import java.util.concurrent.Callable;

import org.jboss.forge.container.services.Exported;

@Exported
public interface UIInput<T>
{
   UICompleter<T> getCompleter();

   String getLabel();

   UIMetadata getMetadata();

   String getName();

   Class<T> getValueType();

   T getValue();

   boolean isEnabled();

   boolean isRequired();

   UIInput<T> setCompleter(UICompleter<T> completer);

   UIInput<T> setDefaultValue(T value);

   UIInput<T> setDefaultValue(Callable<T> callback);

   UIInput<T> setEnabled(boolean b);

   UIInput<T> setEnabled(Callable<Boolean> callable);

   UIInput<T> setLabel(String label);

   UIInput<T> setRequired(boolean required);

   UIInput<T> setRequired(Callable<Boolean> required);

   UIInput<T> setValue(T value);

}
/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;

import java.util.concurrent.Callable;

public interface UIInput<T>
{
   String getName();

   Class<T> getType();

   T getValue();

   boolean isRequired();

   UIInput<T> setDefaultValue(T value);

   UIInput<T> setDefaultValue(Callable<T> callback);

   UIInput<T> setRequired(boolean required);

   UIInput<T> setRequired(Callable<Boolean> required);
}
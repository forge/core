/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.input;

import java.util.concurrent.Callable;

import org.jboss.forge.ui.UICompleter;

/**
 * An {@link UIInput} prompts for a single value.
 *
 *
 * An {@link UICompleter} should be set when you may have N items to select from, and you don't have a specific limit or
 * pre-defined list.<br>
 *
 * <br>
 * If you're interested in prompting for multiple values, see {@link UIInputMany}. <br>
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <VALUETYPE>
 */
public interface UIInput<VALUETYPE> extends UIInputComponent<UIInput<VALUETYPE>, VALUETYPE>
{
   UICompleter<VALUETYPE> getCompleter();

   UIInput<VALUETYPE> setCompleter(UICompleter<VALUETYPE> completer);

   VALUETYPE getValue();

   UIInput<VALUETYPE> setDefaultValue(VALUETYPE value);

   UIInput<VALUETYPE> setDefaultValue(Callable<VALUETYPE> callback);

   UIInput<VALUETYPE> setValue(VALUETYPE value);
}
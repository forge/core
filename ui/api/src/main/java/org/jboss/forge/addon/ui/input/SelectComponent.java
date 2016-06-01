/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import java.util.concurrent.Callable;

import org.jboss.forge.addon.convert.Converter;

/**
 * Parent interface for UISelect components
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <IMPLTYPE>
 * @param <VALUETYPE>
 */
public interface SelectComponent<IMPLTYPE, VALUETYPE> extends InputComponent<IMPLTYPE, VALUETYPE>
{
   Iterable<VALUETYPE> getValueChoices();

   IMPLTYPE setValueChoices(Iterable<VALUETYPE> values);

   IMPLTYPE setValueChoices(Callable<Iterable<VALUETYPE>> values);

   Converter<VALUETYPE, String> getItemLabelConverter();

   IMPLTYPE setItemLabelConverter(Converter<VALUETYPE, String> converter);
}
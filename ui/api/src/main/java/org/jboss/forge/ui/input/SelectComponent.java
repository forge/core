/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.input;

import org.jboss.forge.convert.Converter;

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

   Converter<VALUETYPE, String> getItemLabelConverter();

   IMPLTYPE setItemLabelConverter(Converter<VALUETYPE, String> converter);
}
/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.input;

import java.util.concurrent.Callable;

import org.jboss.forge.convert.Converter;

/**
 * A {@link UISelectMany} should be used when the number of items to be chosen are known before rendering the component.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <VALUETYPE>
 */
public interface UISelectOne<VALUETYPE> extends UIInputComponent<UISelectOne<VALUETYPE>, VALUETYPE>
{
   Iterable<VALUETYPE> getValueChoices();

   UISelectOne<VALUETYPE> setValueChoices(Iterable<VALUETYPE> values);

   VALUETYPE getValue();

   UISelectOne<VALUETYPE> setDefaultValue(VALUETYPE value);

   UISelectOne<VALUETYPE> setDefaultValue(Callable<VALUETYPE> callback);

   UISelectOne<VALUETYPE> setValue(VALUETYPE value);

   Converter<VALUETYPE, String> getItemLabelConverter();

   UISelectOne<VALUETYPE> setItemLabelConverter(Converter<VALUETYPE, String> converter);
}

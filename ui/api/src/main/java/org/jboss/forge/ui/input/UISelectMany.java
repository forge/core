/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.input;

import java.util.concurrent.Callable;

/**
 * A {@link UISelectMany} should be used when the number of items to be chosen are known before rendering the component.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 * @param <VALUETYPE>
 */
public interface UISelectMany<VALUETYPE> extends UISelectComponent<UISelectMany<VALUETYPE>, VALUETYPE>
{
   Iterable<VALUETYPE> getValue();

   UISelectMany<VALUETYPE> setDefaultValue(Iterable<VALUETYPE> value);

   UISelectMany<VALUETYPE> setDefaultValue(Callable<Iterable<VALUETYPE>> callback);

   UISelectMany<VALUETYPE> setValue(Iterable<VALUETYPE> value);
}

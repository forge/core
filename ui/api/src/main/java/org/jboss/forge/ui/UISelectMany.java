/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui;


public interface UISelectMany<VALUETYPE> extends UIInputComponent<UISelectMany<VALUETYPE>, Iterable<VALUETYPE>>
{
   Iterable<VALUETYPE> getValueChoices();

   UISelectMany<VALUETYPE> setValueChoices(Iterable<VALUETYPE> values);
}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui;

public interface UISelectOne<T> extends UIInput<T>
{
   Iterable<T> getValueChoices();

   UISelectOne<T> setValueChoices(Iterable<T> values);
}

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui;

import java.util.Set;

public interface UISelectMany<T> extends UIInput<Set<T>>
{
   Set<T> getValueChoices();

   UISelectMany<T> setValueChoices(Set<T> values);
}

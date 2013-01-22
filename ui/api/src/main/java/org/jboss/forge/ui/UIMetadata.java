/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;

import java.util.Map.Entry;

public interface UIMetadata extends Iterable<Entry<Object, Object>>
{
   public Object get(Object key);

   public UIMetadata set(Object key, Object value);
}
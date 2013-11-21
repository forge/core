/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.input;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.furnace.util.Predicate;

/**
 * Allows registration of a filter for value choices for {@link SelectComponent} injected in {@link UICommand}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface SelectComponentEnhancer
{
   public <T> void registerValueChoiceFilter(Class<T> valueType, Predicate<T> filter);
}

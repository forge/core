/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;

import org.jboss.forge.ui.context.UIContextProvider;
import org.jboss.forge.ui.input.UIInputComponent;

/**
 * Allows {@link UICommand} objects to specify the order of the displayed fields
 *
 * Each wizard page receives an unique instance of {@link UIBuilder}
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface UIBuilder extends UIContextProvider
{
   UIBuilder add(UIInputComponent<?, ?> input);
}

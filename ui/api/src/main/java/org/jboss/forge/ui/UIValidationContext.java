/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;

public interface UIValidationContext extends UIContext
{
   public void addValidationError(UIInputComponent<?, ?> input, String errorMessage);
}

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test.impl;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.InputComponent;

public class UIValidationContextImpl implements UIValidationContext {
	private List<String> errors = new ArrayList<String>();
	private UIContext context;

	public UIValidationContextImpl(UIContext context) {
		this.context = context;
	}

	@Override
	public void addValidationError(InputComponent<?, ?> input,
			String errorMessage) {
		if (errorMessage == null || errorMessage.isEmpty()) {
			errors.add("An internal error occurred and the original message should have been displayed in here. Check logs");
			Thread.dumpStack();
		} else {
			errors.add(errorMessage);
		}
	}

	public List<String> getErrors() {
		return errors;
	}

	@Override
	public UIContext getUIContext() {
		return context;
	}
}

/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test;

import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;

public interface WizardListener
{
   public void wizardExecuted(UIWizard wizard, Result result);
}

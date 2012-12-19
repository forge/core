/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.wizard;

import org.jboss.forge.ui.Result;

public interface UIWizardStep extends UIWizard
{
   /*
    * Called when user clicks “next” or final confirmation of step completion is received.
    */
   public Result next(UIWizardContext context) throws Exception;
}

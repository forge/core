/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.wizard;

import org.jboss.forge.ui.UICommand;

/**
 * An {@link UICommand} that supports multiple steps.
 *
 * Eg: Next, Previous buttons are enabled in Eclipse
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface UIWizard extends UICommand
{
   /**
    * Returns the next wizard step, or null if there are no pages to be displayed next
    */
   Class<? extends UIWizard> getSuccessor();
}

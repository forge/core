/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.test;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.result.Result;

public interface CommandListener
{
   public void commandExecuted(UICommand command, Result result);
}

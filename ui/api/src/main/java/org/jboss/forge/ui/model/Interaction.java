/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui.model;

import java.util.Map;

/**
 * This object encapsulates the interaction of the user with an UI.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface Interaction
{
   public Command getTarget();

   public Map<CommandParameter, String> getValues();

   /**
    * Tests if all the required parameters are set and the data specified on each of them is valid
    */
   public boolean isReadyToExecute();
}

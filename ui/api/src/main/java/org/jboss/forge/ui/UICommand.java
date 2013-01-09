/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;

import org.jboss.forge.container.services.Remote;

@Remote
public interface UICommand
{
   public void initializeUI(UIContext context) throws Exception;

   public void validate(UIValidationContext context);

   public Result execute(UIContext context) throws Exception;
}

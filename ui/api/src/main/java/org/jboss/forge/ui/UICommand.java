/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.ui;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.Result;

@Exported
public interface UICommand
{
   public UICommandMetadata getMetadata();

   public boolean isEnabled(UIContext context);

   public void initializeUI(UIBuilder builder) throws Exception;

   public void validate(UIValidationContext validator);

   public Result execute(UIContext context) throws Exception;
}

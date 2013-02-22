/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager;

import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.services.Exported;

@Exported
public interface AddonManager
{
   public abstract InstallRequest install(AddonId addonId);

   public abstract boolean remove(AddonId entry);
}
/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.watch;

import java.util.Set;

import org.jboss.forge.furnace.addons.AddonId;

/**
 * Watches addons for modifications and reloads them
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface AddonWatchService
{

   void start();

   boolean isStarted();

   void stop();

   Set<AddonId> getMonitoredAddons();
}
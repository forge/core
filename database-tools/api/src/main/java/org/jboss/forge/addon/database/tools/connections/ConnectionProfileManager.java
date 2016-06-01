/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections;

import java.util.Collection;
import java.util.Map;

public interface ConnectionProfileManager
{
   public Map<String, ConnectionProfile> loadConnectionProfiles();

   public void saveConnectionProfiles(Collection<ConnectionProfile> connectionProfiles);
}

package org.jboss.forge.addon.database.tools.connections;

import java.util.Collection;
import java.util.Map;

public interface ConnectionProfileManager
{
   public Map<String, ConnectionProfile> loadConnectionProfiles();

   public void saveConnectionProfiles(Collection<ConnectionProfile> connectionProfiles);
}

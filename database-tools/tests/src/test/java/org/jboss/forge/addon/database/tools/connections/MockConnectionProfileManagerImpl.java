package org.jboss.forge.addon.database.tools.connections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;

import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManagerImpl;

@Alternative @Priority(Integer.MAX_VALUE)
public class MockConnectionProfileManagerImpl extends ConnectionProfileManagerImpl implements ConnectionProfileManager
{
   
   private HashMap<String, ConnectionProfile> profiles;
   
   public MockConnectionProfileManagerImpl() {
      profiles = new HashMap<String, ConnectionProfile>();
      addDummyProfile();
   }
   
   private void addDummyProfile() {
      ConnectionProfile profile = new ConnectionProfile();
      profile.setName("dummy");
      profile.setDialect("dialect");
      profiles.put(profile.getName(), profile);
   }

   @Override
   public Map<String, ConnectionProfile> loadConnectionProfiles()
   {
      return profiles;
   }

   @Override
   public void saveConnectionProfiles(Collection<ConnectionProfile> connectionProfiles)
   {
      profiles.clear();
      for (ConnectionProfile profile : connectionProfiles) {
         profiles.put(profile.getName(), profile);
      }
   }
   
}

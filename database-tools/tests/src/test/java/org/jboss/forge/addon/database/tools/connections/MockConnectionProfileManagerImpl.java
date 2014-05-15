package org.jboss.forge.addon.database.tools.connections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;

@Alternative
@Priority(Integer.MAX_VALUE)
public class MockConnectionProfileManagerImpl implements ConnectionProfileManager
{

   private final HashMap<String, ConnectionProfile> profiles;

   public MockConnectionProfileManagerImpl()
   {
      profiles = new HashMap<>();
      addDummyProfile();
   }

   private void addDummyProfile()
   {
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
      for (ConnectionProfile profile : connectionProfiles)
      {
         profiles.put(profile.getName(), profile);
      }
   }

}

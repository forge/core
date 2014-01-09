package org.jboss.forge.addon.database.tools.connections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;

public class ConnectionProfileManagerImpl implements ConnectionProfileManager
{
   private final static String NAME = "name";
   private final static String DIALECT = "dialect";
   private final static String DRIVER = "driver";
   private final static String PATH_TO_DRIVER = "path-to-driver";
   private final static String URL = "url";
   private final static String USER = "user";

   @Inject
   private Configuration config;

   @Override
   public Map<String, ConnectionProfile> loadConnectionProfiles()
   {
      HashMap<String, ConnectionProfile> result = new HashMap<String, ConnectionProfile>();
      String connectionProfiles = config.getString("connection-profiles");
      if (connectionProfiles != null)
      {
         Node node = XMLParser.parse(connectionProfiles);
         for (Node child : node.getChildren())
         {
            if (!child.getName().equals("connection-profile"))
               continue; // Only profile elements are valid
            ConnectionProfile descriptor = new ConnectionProfile();
            descriptor.setName(child.getAttribute(NAME));
            descriptor.setDialect(child.getAttribute(DIALECT));
            descriptor.setDriver(child.getAttribute(DRIVER));
            descriptor.setPath(child.getAttribute(PATH_TO_DRIVER));
            descriptor.setUrl(child.getAttribute(URL));
            descriptor.setUser(child.getAttribute(USER));
            result.put(descriptor.getName(), descriptor);
         }
      }
      return result;
   }

   @Override
   public void saveConnectionProfiles(Collection<ConnectionProfile> connectionProfiles)
   {
      Node root = new Node("connection-profiles");
      for (ConnectionProfile descriptor : connectionProfiles)
      {
         Node child = root.createChild("connection-profile");
         child.attribute(NAME, descriptor.getName());
         child.attribute(DIALECT, descriptor.getDialect());
         child.attribute(DRIVER, descriptor.getDriver());
         child.attribute(PATH_TO_DRIVER, descriptor.getPath());
         child.attribute(URL, descriptor.getUrl());
         child.attribute(USER, descriptor.getUser());
      }
      if (root.getChildren().isEmpty())
      {
         config.clearProperty("connection-profiles");
      }
      else
      {
         config.setProperty("connection-profiles", XMLParser.toXMLString(root));
      }
   }

}

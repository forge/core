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
   private final static String CONFIG_KEY_NAME = "name";
   private final static String CONFIG_KEY_DIALECT = "dialect";
   private final static String CONFIG_KEY_DRIVER = "driver";
   private final static String CONFIG_KEY_PATH_TO_DRIVER = "path-to-driver";
   private final static String CONFIG_KEY_URL = "url";
   private final static String CONFIG_KEY_USER = "user";
   private final static String CONFIG_KEY_PASSWORD = "pass";

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
            descriptor.setName(child.getAttribute(CONFIG_KEY_NAME));
            descriptor.setDialect(child.getAttribute(CONFIG_KEY_DIALECT));
            descriptor.setDriver(child.getAttribute(CONFIG_KEY_DRIVER));
            descriptor.setPath(child.getAttribute(CONFIG_KEY_PATH_TO_DRIVER));
            descriptor.setUrl(child.getAttribute(CONFIG_KEY_URL));
            descriptor.setUser(child.getAttribute(CONFIG_KEY_USER));
            descriptor.setPassword(child.getAttribute(CONFIG_KEY_PASSWORD));
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
         child.attribute(CONFIG_KEY_NAME, descriptor.getName());
         child.attribute(CONFIG_KEY_DIALECT, descriptor.getDialect());
         child.attribute(CONFIG_KEY_DRIVER, descriptor.getDriver());
         child.attribute(CONFIG_KEY_PATH_TO_DRIVER, descriptor.getPath());
         child.attribute(CONFIG_KEY_URL, descriptor.getUrl());
         child.attribute(CONFIG_KEY_USER, descriptor.getUser());
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

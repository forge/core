/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Strings;
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
   private final static String CONFIG_KEY_SAVE_PASSWORD = "save-password";
   private final static String CONFIG_KEY_ENCRYPTED_PASSWORD = "encrypted-password";

   @Override
   public Map<String, ConnectionProfile> loadConnectionProfiles()
   {
      Configuration config = SimpleContainer.getServices(getClass().getClassLoader(), Configuration.class).get();
      Map<String, ConnectionProfile> result = new LinkedHashMap<String, ConnectionProfile>();
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
            descriptor.setSavePassword(Boolean.parseBoolean(child.getAttribute(CONFIG_KEY_SAVE_PASSWORD)));
            String password = child.getAttribute(CONFIG_KEY_PASSWORD);
            if (Boolean.parseBoolean(child.getAttribute(CONFIG_KEY_ENCRYPTED_PASSWORD)))
            {
               password = decodePassword(password);
            }
            descriptor.setPassword(password);
            result.put(descriptor.getName(), descriptor);
         }
      }
      return result;
   }

   @Override
   public void saveConnectionProfiles(Collection<ConnectionProfile> connectionProfiles)
   {
      Configuration config = SimpleContainer.getServices(getClass().getClassLoader(), Configuration.class).get();
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
         child.attribute(CONFIG_KEY_SAVE_PASSWORD, descriptor.isSavePassword());
         if (descriptor.isSavePassword() && !Strings.isNullOrEmpty(descriptor.getPassword()))
         {
            String encryptedPassword = encodePassword(descriptor.getPassword());
            child.attribute(CONFIG_KEY_PASSWORD, encryptedPassword);
            child.attribute(CONFIG_KEY_ENCRYPTED_PASSWORD, "true");
         }
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

   // weak encryption just to avoid plain text passwords
   // Copied from com.intellij.openapi.util.PasswordUtil.java
   protected String encodePassword(String password)
   {
      StringBuilder result = new StringBuilder();
      if (password != null)
      {
         for (int i = 0; i < password.length(); i++)
         {
            int c = password.charAt(i);
            c ^= 0xdfaa;
            result.append(Integer.toHexString(c));
         }
      }
      return result.toString();
   }

   protected String decodePassword(String password)
   {
      StringBuilder result = new StringBuilder();
      if (password != null)
      {
         for (int i = 0; i < password.length(); i += 4)
         {
            String s = password.substring(i, i + 4);
            int c = Integer.parseInt(s, 16);
            c ^= 0xdfaa;
            result.append((char) c);
         }
      }
      return result.toString();
   }
}
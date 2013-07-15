/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.addon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractTestClass
{
   private static final String ALT_USER_SETTINGS_XML_LOCATION = "org.apache.maven.user-settings";
   private static final String ALT_LOCAL_REPOSITORY_LOCATION = "maven.repo.local";

   @BeforeClass
   public static void setRemoteRepository() throws IOException
   {
      System.setProperty(ALT_USER_SETTINGS_XML_LOCATION, getAbsolutePath("profiles/settings.xml"));
      System.setProperty(ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
   }

   private static String getAbsolutePath(String path) throws FileNotFoundException
   {
      URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
      if (resource == null)
         throw new FileNotFoundException(path);
      return resource.getFile();
   }

   @AfterClass
   public static void clearRemoteRepository()
   {
      System.clearProperty(ALT_USER_SETTINGS_XML_LOCATION);
      System.clearProperty(ALT_LOCAL_REPOSITORY_LOCATION);
   }

}

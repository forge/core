/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.database.tools.connections;

/**
 * Holds information about a Connection
 *
 * @author Forge Team
 */
public class ConnectionProfile
{

   private String name;
   private String dialect;
   private String driver;
   private String path;
   private String url;
   private String user;
   private String password;
   private boolean savePassword;

   public String getName()
   {
      return name;
   }

   public String getDialect()
   {
      return dialect;
   }

   public String getDriver()
   {
      return driver;
   }

   public String getPath()
   {
      return path;
   }

   public String getUrl()
   {
      return url;
   }

   public String getUser()
   {
      return user;
   }

   public String getPassword()
   {
      return password;
   }

   public boolean isSavePassword()
   {
      return savePassword;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setDialect(String dialect)
   {
      this.dialect = dialect;
   }

   public void setDriver(String driver)
   {
      this.driver = driver;
   }

   public void setPath(String path)
   {
      this.path = path;
   }

   public void setUrl(String url)
   {
      this.url = url;
   }

   public void setUser(String user)
   {
      this.user = user;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public void setSavePassword(boolean savePassword)
   {
      this.savePassword = savePassword;
   }

   @Override
   public String toString()
   {
      return "ConnectionProfile [name=" + name + ", dialect=" + dialect + ", driver=" + driver + ", path=" + path
               + ", url=" + url + ", user=" + user + ", password=" + password + ", savePassword=" + savePassword + "]";
   }

}

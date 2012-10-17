package org.jboss.forge.arquillian;

import java.io.File;

import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class ForgeContainerConfiguration implements ContainerConfiguration
{
   String forgeHome = System.getenv("FORGE_HOME");

   public ForgeContainerConfiguration()
   {
      if (forgeHome == null || forgeHome.isEmpty())
      {
         forgeHome = System.getProperty("forge.home");
      }
   }

   @Override
   public void validate() throws ConfigurationException
   {
      if (forgeHome == null)
      {
         throw new ConfigurationException("$FORGE_HOME must be set. Or forgeHome must be set in arquillian.xml");
      }
      else if (!(new File(forgeHome).exists()))
      {
         throw new ConfigurationException("FORGE_HOME [" + forgeHome
                  + "] does not point to a valid Forge installation.");
      }
   }

   public String getForgeHome()
   {
      return forgeHome;
   }

   public void setForgeHome(String forgeHome)
   {
      this.forgeHome = forgeHome;
   }

}

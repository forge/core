package org.jboss.forge.addon.database.tools.generate;

import java.net.URL;
import java.util.Properties;

import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.cdi.CommandScoped;

/**
 * @author <a href="lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@CommandScoped
public class GenerateEntitiesCommandDescriptor
{
   private String targetPackage = "";
   private String connectionProfileName = "";
   private Project selectedProject;
   private ConnectionProfile connectionProfile;
   private URL[] urls;
   private String driverClass;
   private Properties connectionProperties;

   public String getTargetPackage()
   {
      return targetPackage;
   }

   public void setTargetPackage(String targetPackage)
   {
      this.targetPackage = targetPackage;
   }

   public String getConnectionProfileName()
   {
      return connectionProfileName;
   }

   public void setConnectionProfileName(String connectionProfileName)
   {
      this.connectionProfileName = connectionProfileName;
   }

   public Project getSelectedProject()
   {
      return selectedProject;
   }

   public void setSelectedProject(Project selectedProject)
   {
      this.selectedProject = selectedProject;
   }

   public ConnectionProfile getConnectionProfile()
   {
      return connectionProfile;
   }

   public void setConnectionProfile(ConnectionProfile connectionProfile)
   {
      this.connectionProfile = connectionProfile;
   }

   public URL[] getUrls()
   {
      return urls;
   }

   public void setUrls(URL[] urls)
   {
      this.urls = urls;
   }

   public String getDriverClass()
   {
      return driverClass;
   }

   public void setDriverClass(String driverClass)
   {
      this.driverClass = driverClass;
   }

   public Properties getConnectionProperties()
   {
      return connectionProperties;
   }

   public void setConnectionProperties(Properties connectionProperties)
   {
      this.connectionProperties = connectionProperties;
   }
}

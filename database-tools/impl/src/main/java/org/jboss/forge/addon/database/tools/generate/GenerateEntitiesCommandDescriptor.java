package org.jboss.forge.addon.database.tools.generate;

import java.net.URL;
import java.util.Properties;

import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.cdi.CommandScoped;

@CommandScoped
public class GenerateEntitiesCommandDescriptor
{
   String targetPackage = "";
   String connectionProfileName = "";
   Project selectedProject;
   ConnectionProfile connectionProfile;
   URL[] urls;
   String driverClass;
   Properties connectionProperties;

}
